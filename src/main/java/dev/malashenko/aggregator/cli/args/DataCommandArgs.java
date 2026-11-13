package dev.malashenko.aggregator.cli.args;

import dev.malashenko.aggregator.core.model.StorageFormat;
import dev.malashenko.aggregator.core.model.WriteMode;

import java.util.List;

public record DataCommandArgs(List<String> sourceNames, StorageFormat format, WriteMode writeMode, String filename) {}

