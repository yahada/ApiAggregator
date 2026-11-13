package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.StorageFormat;

import java.nio.file.Path;
import java.util.Optional;

public class ShowCommandParser {


	public static ShowCommandArgs parse(String[] args) {
		String filename = getFilename(args);
		Optional<String> apiFilter = getApiFilter(args);
		StorageFormat format = detectFormat(filename);
		return new ShowCommandArgs(filename, apiFilter, format);
	}

	private static String getFilename(String[] args) {
		if (args.length == 0) {
 		throw new ParsingException("'show' command requires a file argument");
		}
		return parseFilename(args[0]);
	}

	private static Optional<String> getApiFilter(String[] args) {
		return parseOptionalApiFilter(args, 1);
	}

	private static StorageFormat detectFormat(String filename) {
		return detectFormat(Path.of(filename));
	}

	private static String parseFilename(String arg) {
		if (arg.startsWith("file=")) {
			return arg.substring("file=".length());
		}
		return arg;
	}

	private static Optional<String> parseOptionalApiFilter(String[] args, int startIndex) {
		for (int i = startIndex; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("API=") || arg.startsWith("--API=")) {
				String cleaned = arg.startsWith("--") ? arg.substring(2) : arg;
				int eq = cleaned.indexOf('=');
				String value = cleaned.substring(eq + 1).trim();
				return Optional.of(value.toLowerCase());
			}
		}
		return Optional.empty();
	}

	private static StorageFormat detectFormat(Path filePath) {
		String filename = filePath.getFileName().toString().toLowerCase();
		if (filename.endsWith(".json")) {
			return StorageFormat.JSON;
		} else if (filename.endsWith(".csv")) {
			return StorageFormat.CSV;
		} else {
			throw new ParsingException("cannot detect format from file: '" + filename + "' (expected .json or .csv)");
		}
	}
}
