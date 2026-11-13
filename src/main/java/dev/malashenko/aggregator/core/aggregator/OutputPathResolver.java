package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.ParsingException;
import dev.malashenko.aggregator.core.model.StorageFormat;
import dev.malashenko.aggregator.core.tools.FileExtension;

import java.nio.file.Path;

public class OutputPathResolver {

	public static Path resolve(String filename, StorageFormat storageFormat) {
		String expectedExtension = FileExtension.formatOf(storageFormat);

		if (filename == null || filename.isBlank()) {
			return Path.of("data." + expectedExtension);
		}

		String actualExtension = FileExtension.extension(filename);
		if (actualExtension.isEmpty()) {
			return Path.of(filename + "." + expectedExtension);
		}

		if (!actualExtension.equalsIgnoreCase(expectedExtension)) {
			throw new ParsingException(
				"File extension '" + actualExtension + "' does not match format '"
				+ expectedExtension + "'. Expected: " + filename + "." + expectedExtension
			);
		}

		return Path.of(filename);
	}
}
