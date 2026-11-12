package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.CommandArgs;
import dev.malashenko.aggregator.cli.parsers.CommandParser;
import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.model.DataRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CommandExecutor {

	private final CommandParser commandParser;
	private final Aggregator aggregator;

	public CommandExecutor(CommandParser commandParser, Aggregator aggregator) {
		this.commandParser = commandParser;
		this.aggregator = aggregator;
	}

	public void execute(String line) {
		try {
			CommandArgs commandArgs = commandParser.parse(line);

			switch (commandArgs) {
 			case DataCommandArgs data -> {
 				aggregator.makeRequest(data.entityType(), data.apiParams());
 				log.info("Data was written");
 			}
 			case ShowCommandArgs show -> {
 				List<DataRecord> records = show.optional()
 					.map(aggregator::readData)
 					.orElseGet(() -> aggregator.readData(null));
 				records.forEach(record -> log.info(record.toString()));
 			}
			}

		} catch (IllegalArgumentException | IllegalStateException e) {
			log.error("Got an exception: {}", e.getMessage());
		}
	}
}
