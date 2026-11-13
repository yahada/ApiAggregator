package dev.malashenko.aggregator.cli.args;

import dev.malashenko.aggregator.core.model.StorageFormat;

import java.util.Optional;

public record ShowCommandArgs(String filename, Optional<String> apiFilter, StorageFormat format) {}
