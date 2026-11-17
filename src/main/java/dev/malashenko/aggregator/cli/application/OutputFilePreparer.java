package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.model.WriteMode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class OutputFilePreparer {

	public void prepare(Path file, WriteMode mode) {
		if (file == null) {
			throw new AppException("File path must not be null");
		}

		try {
			Path parent = file.toAbsolutePath().getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}

			if (mode == WriteMode.NEW) {
				if (Files.exists(file)) {
					Files.delete(file);
				}

				Files.createFile(file);
				log.info("Created new file: {}", file);
			} else {
				log.info("Appending to existing file: {}", file);
			}
		} catch (IOException e) {
			throw new AppException("Failed to prepare file " + file + ": " + e.getMessage(), e);
		}
	}
}
