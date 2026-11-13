package dev.malashenko.aggregator;

import dev.malashenko.aggregator.cli.application.CliApplication;
import dev.malashenko.aggregator.core.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			CliApplication application = new CliApplication();
			int exitCode = application.run(args);
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
