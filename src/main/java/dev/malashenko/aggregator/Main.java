package dev.malashenko.aggregator;

import lombok.extern.slf4j.Slf4j;

import dev.malashenko.aggregator.factory.AppFactory;
import dev.malashenko.aggregator.cli.CliApplication;
import dev.malashenko.aggregator.aggregator.Aggregator;

import java.io.IOException;
import java.net.http.HttpClient;

@Slf4j
public class Main {

	public static void main(String[] args) {
		log.info("ApiAggregator starting with args: {}", java.util.Arrays.toString(args));
		try (HttpClient client = HttpClient.newHttpClient()) {

			AppFactory appFactory = new AppFactory(client);
			Aggregator aggregator = appFactory.createAggregator();

			CliApplication application = new CliApplication(aggregator, appFactory);

			application.run(args);
			log.info("ApiAggregator finished successfully");
		} catch (IOException e) {
			log.error("IO error: {}", e.getMessage(), e);
			System.exit(1);
		} catch (InterruptedException e) {
			log.error("Interrupted: {}", e.getMessage(), e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log.error("Argument error: {}", e.getMessage());
			System.exit(2);
		}
	}
}
