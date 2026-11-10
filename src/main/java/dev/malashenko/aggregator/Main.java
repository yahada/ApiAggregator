package dev.malashenko.aggregator;

import dev.malashenko.aggregator.cli.commands.Command;
import dev.malashenko.aggregator.cli.parsers.*;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.aggregator.AggregatorBuilder;

import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;

import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.coinmarketcap.CoinMarketCapHttpApiSource;
import dev.malashenko.aggregator.core.source.coinmarketcap.CoinMarketCapMapper;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsHttpApiSource;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsMapper;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsHttpApiSource;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsMapper;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static dev.malashenko.aggregator.core.aggregator.AggregatorBuilder.httpResponder;
import static dev.malashenko.aggregator.core.aggregator.AggregatorBuilder.objectMapper;

public class Main {
	public static void main(String[] args) {

		InitialArgs parsed = null;
		InitialArgsParser argsParser = new InitialArgsParser();

		try {
			parsed = argsParser.getArgs(args);

		} catch (IllegalArgumentException e) {
			System.err.println("error: " + e.getMessage());
			System.err.println("usage: sources=<csv> format=<csv|json> mode=<new|append> file=<path>");
			System.exit(2);
		}

		Aggregator aggregator = AggregatorBuilder.buildAggregator(parsed.apiList(), parsed.format(), parsed.writeMode(), parsed.filename());

		CommandParser commandParser = new CommandParser(aggregator.getApiSources());
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("ready. commands: data API=<name> [param=value ...] | show [API=<name>]");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.isBlank()) continue;
				if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;

				CommandArgs commandArgs =
			}


		} catch (IllegalArgumentException e) {
		System.err.println("error: " + e.getMessage());
		} catch (RuntimeException e) {
		System.err.println("failed: " + e.getMessage());
	}

		RequestParams params = new RequestParams(Map.of(
			"tags", "story",
			"hitsPerPage", "11",
			"numericFilters", "points>2"
		));

		aggregator.makeRequest(EntityType.NEWS, params);


	}
}
