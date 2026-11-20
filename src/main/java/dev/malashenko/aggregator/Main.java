package dev.malashenko.aggregator;

import dev.malashenko.aggregator.cli.application.CliApplication;
import dev.malashenko.aggregator.core.exception.AppException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) {
		try {
			CliApplication application = new CliApplication();
			int exitCode = application.run(args);
			log.info("Finished with csode {}", exitCode);
			if (exitCode != 0) {
				System.exit(exitCode);
			}
		} catch (AppException e) {
			log.error("Execution error: {}", e.getMessage());
			System.exit(1);
		} catch (RuntimeException e) {
			log.error("Unexpected error: {}", e.getMessage());
			System.exit(1);
		}
	}
}
