package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.InitialArgs;
import dev.malashenko.aggregator.cli.parsers.InitialArgsParser;
import dev.malashenko.aggregator.cli.parsers.ShowCommandParser;
import dev.malashenko.aggregator.core.aggregator.AggregateService;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.aggregator.AggregatorBuilder;
import dev.malashenko.aggregator.core.aggregator.OutputPathResolver;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.SourceTypeRegistry;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorageFactory;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CliApplication {

	private static final String USAGE_DATA =
		"usage: data sources=<api1,api2,...> format=<csv|json> mode=<new|append> [file=<path>]";

	private static final String USAGE_SHOW =
		"usage: show <file> [API=<name>]";

	private static final String USAGE =
		"""
			Available commands:
			  data  — fetch data from API sources and save to file
			  show  — display records from a saved file
			
			Run a command with invalid arguments to see its detailed usage.""";

	public int run(String[] args) {
		if (args.length == 0) {
			log.error("error: no command provided");
			log.error(USAGE);
			return 2;
		}



		String command = args[0].toLowerCase();

		return switch (command) {
			case "data" -> runData(args);
			case "show" -> runShow(args);
			default -> {
				log.error("error: unknown command: '{}'", command);
				log.error(USAGE);
				yield 2;
			}
		};
	}

	private int runData(String[] args) {
		try {
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);

			InitialArgsParser argsParser = new InitialArgsParser();
			InitialArgs parsed = argsParser.getArgs(remainingArgs);

			new OutputFilePreparer().prepare(parsed);

			AggregatorBuilder builder = AggregatorBuilder.createDefault();
			Aggregator aggregator = builder.buildAggregator(
				parsed.apiList(),
				parsed.format(),
				parsed.writeMode(),
				parsed.filename()
			);

			runSurveyAndRequests(aggregator);

			return 0;

		} catch (ParsingException e) {
			log.error("error: {}", e.getMessage());
			log.error(USAGE_DATA);
			return 2;
		} catch (AppException e) {
			log.error("error: {}", e.getMessage(), e);
			return 1;
		}
	}

	private void runSurveyAndRequests(Aggregator aggregator) {
		Map<EntityType, ApiSource> apiSourceMap = new EnumMap<>(EntityType.class);

		for (ApiSource source : aggregator.getApiSources()) {
			EntityType type = resolveEntityType(source.id());
			apiSourceMap.put(type, source);
			log.info("API: {} — default params: {}", source.id(), source.defaultParams());
		}

		new AggregateService(aggregator).aggregate(apiSourceMap);
	}

	private EntityType resolveEntityType(String sourceId) {
		EntityType type = SourceTypeRegistry.of(sourceId);
		if (type == null) {
			throw new AppException("unknown source: '" + sourceId + "'");
		}
		return type;
	}

	private int runShow(String[] args) {
		try {
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);

			ShowCommandParser parser = new ShowCommandParser(remainingArgs);
			String filename = parser.getFilename();
			Optional<EntityType> apiFilter = parser.getApiFilter();
			StorageFormat format = parser.detectFormat(filename);

			Aggregator aggregator = buildAggregatorForRead(filename, format);
			List<DataRecord> records = aggregator.readData(apiFilter.orElse(null));

			new RecordPrinter().printAll(records);

			return 0;

		} catch (ParsingException e) {
			log.error("error: {}", e.getMessage());
			log.error(USAGE_SHOW);
			return 2;
		} catch (AppException e) {
			log.error("error: {}", e.getMessage(), e);
			return 1;
		}
	}

	private Aggregator buildAggregatorForRead(String filename, StorageFormat format) {
		var dataStorage = DataStorageFactory.getDataStorage(format, ObjectMapperBuilder.buildDefault());
		Path outputPath = new OutputPathResolver().resolve(filename, format);
		return new Aggregator(dataStorage, new EnumMap<>(EntityType.class), outputPath);
	}
}
