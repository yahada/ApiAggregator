package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.source.ApiSourceRegistry;
import dev.malashenko.aggregator.core.model.StorageFormat;
import dev.malashenko.aggregator.core.model.WriteMode;
import dev.malashenko.aggregator.core.tools.FileExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataCommandParser extends CommandParser {

	public static DataCommandArgs parse(String[] args) {
		Map<String, String> params = parseNameValue(args);

		List<String> sourceNames = parseSourceNames(require(params, "sources"));

		StorageFormat format = parseStorageFormat(require(params, "format"));

		WriteMode writeMode = parseWriteMode(require(params, "mode"));

		String filename = params.getOrDefault("file", "out." + FileExtension.formatOf(format));

		return new DataCommandArgs(sourceNames, format, writeMode, filename);
	}

	private static List<String> parseSourceNames(String raw) {
		return Arrays.stream(raw.split(","))
			       .map(String::trim)
			       .filter(s -> !s.isEmpty())
			       .map(String::toLowerCase)
			       .peek(DataCommandParser::validateSource)
			       .toList();
	}

	private static void validateSource(String name) {
		if (!ApiSourceRegistry.SOURCE_NAMES.contains(name)) {
			throw new ParsingException("unknown source: '" + name + "'; available: " + ApiSourceRegistry.SOURCE_NAMES);
		}
	}

	private static StorageFormat parseStorageFormat(String s) {
		return switch (s.toLowerCase()) {
			case "csv"  -> StorageFormat.CSV;
			case "json" -> StorageFormat.JSON;
			default -> throw new ParsingException("unknown format: '" + s + "' (csv|json)");
		};
	}

	private static WriteMode parseWriteMode(String s) {
		return switch (s.toLowerCase()) {
			case "new"    -> WriteMode.NEW;
			case "append" -> WriteMode.APPEND;
			default -> throw new ParsingException("unknown mode: '" + s + "' (new|append)");
		};
	}
}