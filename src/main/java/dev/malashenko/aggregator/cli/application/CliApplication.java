package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.InitialArgs;
import dev.malashenko.aggregator.cli.parsers.*;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.aggregator.AggregatorBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CliApplication {

	private static final String USAGE = "usage: sources=<csv> format=<csv|json> " + "mode=<new|append> file=<path>";

	public int run(String[] args) {
		InitialArgs parsed;

		try {
			InitialArgsParser argsParser = new InitialArgsParser();
			parsed = argsParser.getArgs(args);

 	} catch (IllegalArgumentException e) {
			log.error("error: {}", e.getMessage());
			log.error(USAGE);
			return 2;
		}

		new OutputFilePreparer().prepare(parsed);

		try {
			AggregatorBuilder builder = AggregatorBuilder.createDefault();

			Aggregator aggregator = builder.buildAggregator(
				parsed.apiList(),
				parsed.format(),
				parsed.writeMode(),
				parsed.filename()
			);

			CommandParser commandParser = new CommandParser(aggregator.getApiSources());
			CommandExecutor commandExecutor = new CommandExecutor(commandParser, aggregator);
			new CommandLoop(commandExecutor).start();

			return 0;

		} catch (RuntimeException e) {
			log.error("error: {}", e.getMessage(), e);
			return 1;
		}
	}
}
