package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InitialArgsParser {
	private static final Map<String, EntityType> SOURCE_TO_TYPE = Map.of(
		"kitsu", EntityType.ANIME,
		"usgs",          EntityType.EARTHQUAKE,
		"hackernews",    EntityType.NEWS
	);


	public InitialArgs getArgs(String[] args) {
		Map<String, String> params = parseNameValue(args);

		List<EntityType> apiList = parseEntityTypes(require(params, "sources"));
		StorageFormat format     = parseFormat(require(params, "format"));

		WriteMode writeMode      = parseWriteMode(require(params, "mode"));

		String filename          = require(params, "file");

		return new InitialArgs(apiList, format, writeMode, filename);
	}


	 static Map<String, String> parseNameValue(String[] args) {
		Map<String, String> result = new LinkedHashMap<>();
		for (String arg : args) {
			String cleaned = arg.startsWith("--") ? arg.substring(2) : arg;
			int eq = cleaned.indexOf('=');
			if (eq <= 0 || eq == cleaned.length() - 1) {
				throw new IllegalArgumentException(
					"bad argument: '" + arg + "' (expected name=value)");
			}
			String key = cleaned.substring(0, eq).trim();
			String value = cleaned.substring(eq + 1).trim();
			if (key.isEmpty() || value.isEmpty()) {
				throw new IllegalArgumentException(
					"bad argument: '" + arg + "' (empty name or value)");
			}
			result.put(key, value);
		}
		return result;
	}

	 static List<EntityType> parseEntityTypes(String raw) {
		return Arrays.stream(raw.split(","))
			       .map(String::trim)
			       .filter(s -> !s.isEmpty())
			       .map(InitialArgsParser::toEntityType)
			       .toList();
	}

	 static EntityType toEntityType(String s) {
		EntityType t = SOURCE_TO_TYPE.get(s.toLowerCase(Locale.ROOT));
		if (t == null) {
			throw new IllegalArgumentException(
				"unknown source: '" + s + "'; available: " + SOURCE_TO_TYPE.keySet());
		}
		return t;
	}

	 static StorageFormat parseFormat(String s) {
		return switch (s.toLowerCase(Locale.ROOT)) {
			case "csv"  -> StorageFormat.CSV;
			case "json" -> StorageFormat.JSON;
			default -> throw new IllegalArgumentException(
				"unknown format: '" + s + "' (csv|json)");
		};
	}

	 static WriteMode parseWriteMode(String s) {
		return switch (s.toLowerCase(Locale.ROOT)) {
			case "new"    -> WriteMode.NEW;
			case "append" -> WriteMode.APPEND;
			default -> throw new IllegalArgumentException(
				"unknown mode: '" + s + "' (new|append)");
		};
	}

	 static String require(Map<String, String> params, String key) {
		String v = params.get(key);
		if (v == null || v.isBlank()) {
			throw new IllegalArgumentException(
				"missing required argument: " + key +
					"; expected: sources=... format=... mode=... file=...");
		}
		return v;
	}
}