package dev.malashenko.aggregator.cli.commands;

import dev.malashenko.aggregator.core.aggregator.Aggregator;

import java.util.Map;

public interface Command {
	void execute(Aggregator aggregator, Map<String, Object> params);
}
