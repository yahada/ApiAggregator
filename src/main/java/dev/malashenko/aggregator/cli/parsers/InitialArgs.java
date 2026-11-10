package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.util.List;

public record InitialArgs(List<EntityType> apiList, StorageFormat format, WriteMode writeMode, String filename) {}

