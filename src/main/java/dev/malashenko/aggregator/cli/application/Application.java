package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.parsers.*;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.aggregator.AggregatorBuilder;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Application {

	private static final String USAGE = "usage: sources=<csv> format=<csv|json> " + "mode=<new|append> file=<path>";

	public int run(String[] args) {
		InitialArgs parsed;

		try {
			InitialArgsParser argsParser = new InitialArgsParser();
			parsed = argsParser.getArgs(args);

		} catch (IllegalArgumentException e) {
			System.err.println("error: " + e.getMessage());
			System.err.println(USAGE);
			return 2;
		}

		prepareFile(parsed);

		try {
			AggregatorBuilder builder = AggregatorBuilder.createDefault();

			Aggregator aggregator = builder.buildAggregator(
				parsed.apiList(),
				parsed.format(),
				parsed.writeMode(),
				parsed.filename()
			);

			runCommandLoop(aggregator);

			return 0;

		} catch (RuntimeException e) {
			System.err.println("error: " + e.getMessage());
			return 1;
		}
	}

	private void prepareFile(InitialArgs parsed) {
		if (parsed.writeMode() != WriteMode.NEW) {
			return;
		}

		Path path = Path.of(parsed.filename());

		try {
			Files.createDirectories(path.toAbsolutePath().getParent());

			Files.write(
				path,
				new byte[0],
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE
			);

		} catch (IOException e) {
			throw new RuntimeException("Failed to prepare file: " + path, e);
		}
	}

	private void runCommandLoop(Aggregator aggregator) {
		CommandParser commandParser =
			new CommandParser(aggregator.getApiSources());

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("ready. commands: data API=<name> " + "[param=value ...] | show [API=<name>]"
			);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.isBlank()) {
					continue;
				}

				if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
					break;
				}

				executeCommand(line, commandParser, aggregator);
			}
		}
	}

	private void executeCommand(String line, CommandParser commandParser, Aggregator aggregator) {
		try {
			CommandArgs commandArgs = commandParser.parse(line);

			switch (commandArgs) {
				case DataCommandArgs data -> {
					aggregator.makeRequest(data.entityType(), data.apiParams());
					System.out.println("Data was written" );
				}
				case ShowCommandArgs show -> aggregator.readData(show.optional());
			}

		} catch (IllegalArgumentException | IllegalStateException e) {
			System.err.println("Got an exception: " + e.getLocalizedMessage());
		}
	}
}