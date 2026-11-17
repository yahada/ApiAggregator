package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.StorageFormat;

import java.nio.file.Path;
import java.util.Map;

public class ShowCommandParser extends CommandParser {

	public static ShowCommandArgs parse(String[] args) {
		Map<String, String> params = parseNameValue(args);

		String filename = require(params, "file");
		StorageFormat format = detectFormat(Path.of(filename));
		String apiFilter = params.get("source");
		if (apiFilter != null) {
			apiFilter = apiFilter.toLowerCase();
		}

		return new ShowCommandArgs(filename, apiFilter, format);
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
