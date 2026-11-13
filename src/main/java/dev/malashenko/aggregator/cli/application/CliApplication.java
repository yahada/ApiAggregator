package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.cli.parsers.DataCommandParser;
import dev.malashenko.aggregator.cli.parsers.ShowCommandParser;
import dev.malashenko.aggregator.core.aggregator.AggregateService;
import dev.malashenko.aggregator.core.aggregator.DataAggregator;
import dev.malashenko.aggregator.core.aggregator.OutputPathResolver;
import dev.malashenko.aggregator.core.aggregator.ShowAggregator;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceRegistry;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageFactory;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

			DataCommandArgs parsed = DataCommandParser.parse(remainingArgs);

			OutputFilePreparer.prepare(parsed);

			HttpResponder httpResponder = HttpResponder.getResponder();

			Map<String, ApiSource> allSources = ApiSourceRegistry.createAll(httpResponder);

			Map<String, ApiSource> selected = new LinkedHashMap<>();
			for (String name : parsed.sourceNames()) {
				ApiSource source = allSources.get(name);
				if (source == null) {
					throw new ParsingException("unknown source: '" + name + "'");
				}
				selected.put(name, source);
			}

			DataStorage dataStorage = DataStorageFactory.getDataStorage(parsed.format());
			Path outputPath = OutputPathResolver.resolve(parsed.filename(), parsed.format());

			DataAggregator aggregator = new DataAggregator(dataStorage, allSources, outputPath);

			runSurveyAndRequests(aggregator, selected);

			return 0;

		} catch (ParsingException e) {
			log.error("Data command parsing error: {}", e.getMessage());
			log.error(USAGE_DATA);
			return 2;
		} catch (AppException e) {
			log.error("Data command error: {}", e.getMessage(), e);
			return 1;
		}
	}

	private void runSurveyAndRequests(DataAggregator aggregator, Map<String, ApiSource> selectedSources) {
		for (Map.Entry<String, ApiSource> entry : selectedSources.entrySet()) {
			ApiSource source = entry.getValue();
			log.info("API: {} — default params: {}", source.id(), source.defaultParams());
		}

		AggregateService.aggregate(aggregator, selectedSources);
	}

	private int runShow(String[] args) {
		try {
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);

			ShowCommandArgs parsed = ShowCommandParser.parse(remainingArgs);

			DataStorage dataStorage = DataStorageFactory.getDataStorage(parsed.format());
			Path outputPath = OutputPathResolver.resolve(parsed.filename(), parsed.format());

			ShowAggregator aggregator = new ShowAggregator(dataStorage, outputPath);
			List<DataRecord> records = aggregator.readData(parsed.apiFilter().orElse(null));

			new RecordPrinter().printAll(records);

			return 0;

		} catch (ParsingException e) {
			log.error("Show command parsing error: {}", e.getMessage());
			log.error(USAGE_SHOW);
			return 2;
		} catch (AppException e) {
			log.error("Show command  error: {}", e.getMessage(), e);
			return 1;
		}
	}
}
