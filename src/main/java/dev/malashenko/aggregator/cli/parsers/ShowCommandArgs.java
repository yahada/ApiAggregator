package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.EntityType;

import java.util.Optional;

public record ShowCommandArgs(Optional<EntityType> optional) implements CommandArgs {}
