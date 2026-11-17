package dev.malashenko.aggregator.cli.args;

import dev.malashenko.aggregator.core.model.StorageFormat;

public record ShowCommandArgs(String filename, String apiFilter, StorageFormat format) {}
