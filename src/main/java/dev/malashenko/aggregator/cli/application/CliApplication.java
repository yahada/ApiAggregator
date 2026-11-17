package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.cli.parsers.DataCommandParser;
import dev.malashenko.aggregator.cli.parsers.ShowCommandParser;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.DataRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CliApplication {

	private static final String USAGE_DATA = "usage: data sources=<api1,api2,...> format=<csv|json> mode=<new|append> [file=<path>]";

	private static final String USAGE_SHOW = "usage: show file=<path> [source=<name>]";

	private static final String USAGE =
		"""
			Available commands:
			  data  — fetch data from API sources and save to file
			  show  — display records from a saved file
			
			Run a command with invalid arguments to see its detailed usage.""";

	private final AppFactory appFactory = new AppFactory();

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
			Aggregator aggregator = appFactory.createForData(parsed);

			aggregator.makeRequest();
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

	private int runShow(String[] args) {
		try {
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);

			ShowCommandArgs parsed = ShowCommandParser.parse(remainingArgs);
			Aggregator aggregator = appFactory.createForShow(parsed);

			List<DataRecord> records = aggregator.readData(parsed.apiFilter());
			RecordPrinter.printAll(records);
			return 0;

		} catch (ParsingException e) {
			log.error("Show command parsing error: {}", e.getMessage());
			log.error(USAGE_SHOW);
			return 2;
		} catch (AppException e) {
			log.error("Show command error: {}", e.getMessage(), e);
			return 1;
		}
	}
}
