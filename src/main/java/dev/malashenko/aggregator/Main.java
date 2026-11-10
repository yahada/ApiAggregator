package dev.malashenko.aggregator;
import dev.malashenko.aggregator.cli.parsers.*;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.aggregator.AggregatorBuilder;
import java.util.Scanner;


public class Main {
	public static void main(String[] args) {

		InitialArgs parsed = null;
		InitialArgsParser argsParser = new InitialArgsParser();

		try {
			parsed = argsParser.getArgs(args);

		} catch (IllegalArgumentException e) {
			System.err.println("error: " + e.getMessage());
			System.err.println("usage: sources=<csv> format=<csv|json> mode=<new|append> file=<path>");
			System.exit(2);
		}

		Aggregator aggregator = AggregatorBuilder.buildAggregator(parsed.apiList(), parsed.format(), parsed.writeMode(), parsed.filename());

		CommandParser commandParser = new CommandParser(aggregator.getApiSources());
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("ready. commands: data API=<name> [param=value ...] | show [API=<name>]");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.isBlank()) continue;
				if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;

				try {
					CommandArgs commandArgs = commandParser.parse(line);
					switch (commandArgs) {
						case DataCommandArgs dataCommandArgs -> aggregator.makeRequest(dataCommandArgs.entityType(), dataCommandArgs.apiParams());
						case ShowCommandArgs showCommandArgs -> aggregator.readData(showCommandArgs.optional());
					}
				} catch (IllegalArgumentException e) {
					System.err.println("Got an exception: " + e);
				}

			}
		} catch (RuntimeException e) {
		System.err.println("error: " + e.getMessage());
		}

	}
}
