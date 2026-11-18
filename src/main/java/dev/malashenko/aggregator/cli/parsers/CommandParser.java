package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.exception.ParsingException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommandParser {

	protected static Map<String, String> parseNameValue(String[] args) {
		Map<String, String> result = new HashMap<>();
		for (String arg : args) {
			String cleaned = arg.startsWith("--") ? arg.substring(2) : arg;
			int eq = cleaned.indexOf('=');

			if (eq <= 0 || eq == cleaned.length() - 1) {
				throw new ParsingException("bad argument: '" + arg + "' (expected name=value)");
			}

			String key = cleaned.substring(0, eq).trim();
			String value = cleaned.substring(eq + 1).trim();

			if (key.isEmpty() || value.isEmpty()) {
				throw new ParsingException("bad argument: '" + arg + "' (empty name or value)");
			}
			result.put(key, value);
		}
		return result;
	}

	protected static String require(Map<String, String> params, String key) {
		String v = params.get(key);
		if (v == null || v.isBlank()) {
			throw new ParsingException("missing required argument: " + key);
		}
		return v;
	}
}
