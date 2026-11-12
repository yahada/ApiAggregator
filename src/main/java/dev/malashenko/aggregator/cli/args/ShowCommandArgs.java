package dev.malashenko.aggregator.cli.args;

import dev.malashenko.aggregator.core.model.EntityType;

import java.util.Optional;

public record ShowCommandArgs(Optional<EntityType> optional) implements CommandArgs {}
