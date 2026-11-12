package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;

import java.util.*;

public class CommandParser {

	private static final Map<String, EntityType> SOURCE_TO_TYPE = Map.of(
		"kitsu", EntityType.ANIME,
		"usgs",          EntityType.EARTHQUAKE,
		"hackernews",    EntityType.NEWS
	);

	private final Map<String, Set<String>> knownParamsByApi;

	public CommandParser(List<ApiSource> sources) {
		Map<String, Set<String>> m = new HashMap<>();
		for (ApiSource s : sources) {
			m.put(s.id(), s.knownParams());
		}
		this.knownParamsByApi = Map.copyOf(m);
	}

	/** для тестов */
	public CommandParser(Map<String, Set<String>> knownParamsByApi) {
		this.knownParamsByApi = Map.copyOf(knownParamsByApi);
	}

	public CommandArgs parse(String line) {
		if (line == null || line.isBlank()) {
			throw new IllegalArgumentException("empty command");
		}
		String[] tokens = line.trim().split("\\s+");
		String command = tokens[0];
		Map<String, String> params = InitialArgsParser.parseNameValue(
			Arrays.copyOfRange(tokens, 1, tokens.length));

		return switch (command) {
			case "data" -> parseData(params);
			case "show" -> parseShow(params);
			default -> throw new IllegalArgumentException(
				"unknown command: '" + command + "' (expected: data | show)");
		};
	}

	// ---------- data API=<name> [param=value ...] ----------

	private DataCommandArgs parseData(Map<String, String> params) {
		String api = requireApi(params);            // обязательно
		EntityType entityType = SOURCE_TO_TYPE.get(api);
		Set<String> known = knownParamsByApi.get(api);

		Map<String, String> apiParams = new LinkedHashMap<>(params);
		apiParams.remove("API");                    // убираем служебное

		List<String> unknown = apiParams.keySet().stream()
			                       .filter(k -> !known.contains(k))
			                       .sorted()
			                       .toList();
		if (!unknown.isEmpty()) {
			throw new IllegalArgumentException(
				"unknown parameters for API '" + api + "': " + unknown
					+ "; allowed: " + new TreeSet<>(known));
		}

		return new DataCommandArgs(api, entityType, new RequestParams(apiParams));
	}

	// ---------- show [API=<name>] ----------

	private ShowCommandArgs parseShow(Map<String, String> params) {
		List<String> unknown = params.keySet().stream()
			                       .filter(k -> !k.equals("API"))
			                       .sorted()
			                       .toList();

		if (!unknown.isEmpty()) {
			throw new IllegalArgumentException("unknown parameters for 'show': " + unknown + "; allowed: [API]");
		}

		Optional<EntityType> entityType = Optional.ofNullable(params.get("API"))
			                                  .map(api -> {
				                                  EntityType t = SOURCE_TO_TYPE.get(api);
				                                  if (t == null) {
					                                  throw new IllegalArgumentException(
						                                  "unknown API: '" + api + "'; available: "
							                                  + SOURCE_TO_TYPE.keySet());
				                                  }
				                                  return t;
			                                  });
		
		return new ShowCommandArgs(entityType);
	}

	// ---------- helpers ----------

	private String requireApi(Map<String, String> params) {
		String api = params.get("API");
		if (api == null || api.isBlank()) {
			throw new IllegalArgumentException("missing required parameter: API; available: " + knownParamsByApi.keySet());
		}
		requireApiExists(api);
		return api;
	}

	private void requireApiExists(String api) {
		if (!knownParamsByApi.containsKey(api)) {
			throw new IllegalArgumentException(
				"unknown API: '" + api + "'; available: " + knownParamsByApi.keySet());
		}
	}
}