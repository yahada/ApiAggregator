package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;

public record DataCommandArgs(String apiName, EntityType entityType, RequestParams apiParams)
	implements CommandArgs {}
