package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.model.WriteMode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class OutputFilePreparer {

	public static void prepare(DataCommandArgs args) {
		Path path = Path.of(args.filename());

		try {
			log.info("Preparing output file: {}", path);
			Files.createDirectories(path.toAbsolutePath().getParent());

			if (args.writeMode() == WriteMode.NEW) {
				log.info("Write mode NEW: truncating file");
				Files.write(
					path,
					new byte[0],
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE
				);
			} else if (!Files.exists(path)) {
				log.info("Write mode APPEND: creating new file");
				Files.write(
					path,
					new byte[0],
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE
				);
			} else {
				log.info("Write mode APPEND: file already exists, keeping data");
			}

		} catch (IOException e) {
			throw new AppException("Failed to prepare file: " + path, e);
		}
	}
}
