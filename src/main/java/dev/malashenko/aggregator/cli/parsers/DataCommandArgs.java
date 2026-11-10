package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.RequestParams;

public record DataCommandArgs(String apiName, RequestParams apiParams)
	implements CommandArgs {}
