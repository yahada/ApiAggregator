package dev.malashenko.aggregator.cli.commands;

import dev.malashenko.aggregator.core.aggregator.Aggregator;

import java.util.Map;

public class ShowCommand implements Command {

	@Override
	public void execute(Aggregator aggregator, Map<String, Object> params) {
		if (params == null) {
			aggregator.readData();
			return;
		}
	}




}
