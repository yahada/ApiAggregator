package dev.malashenko.aggregator.cli.parsers;

import java.util.Optional;

public record ShowCommandArgs(Optional<String> apiName)
	implements CommandArgs {}
