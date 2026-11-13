package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.SourceTypeRegistry;
import dev.malashenko.aggregator.core.storage.StorageFormat;

import java.nio.file.Path;
import java.util.Optional;

public class ShowCommandParser {

	private final String[] args;

	public ShowCommandParser(String[] args) {
		this.args = args;
	}

	public String getFilename() {
		if (args.length == 0) {
 		throw new ParsingException("'show' command requires a file argument");
		}
		return parseFilename(args[0]);
	}

	public Optional<EntityType> getApiFilter() {
		return parseOptionalApiFilter(args, 1);
	}

	public StorageFormat detectFormat(String filename) {
		Path filePath = Path.of(filename);
		return detectFormat(filePath);
	}

	private String parseFilename(String arg) {
		if (arg.startsWith("file=")) {
			return arg.substring("file=".length());
		}
		return arg;
	}

	private Optional<EntityType> parseOptionalApiFilter(String[] args, int startIndex) {
		for (int i = startIndex; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("API=") || arg.startsWith("--API=")) {
				String cleaned = arg.startsWith("--") ? arg.substring(2) : arg;
				int eq = cleaned.indexOf('=');
				String value = cleaned.substring(eq + 1).trim();
				EntityType type = SourceTypeRegistry.of(value);
				if (type == null) {
 				throw new ParsingException(
 					"unknown API: '" + value + "'; available: " + SourceTypeRegistry.sourceNames()
 				);
				}
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}

	private StorageFormat detectFormat(Path filePath) {
		String filename = filePath.getFileName().toString().toLowerCase();
		if (filename.endsWith(".json")) {
			return StorageFormat.JSON;
		} else if (filename.endsWith(".csv")) {
			return StorageFormat.CSV;
		} else {
			throw new ParsingException(
				"cannot detect format from file: '" + filename + "' (expected .json or .csv)"
			);
		}
	}
}
