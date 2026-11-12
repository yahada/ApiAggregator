package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.CommandArgs;
import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.model.SourceTypeRegistry;
import dev.malashenko.aggregator.core.source.ApiSource;

import java.util.*;

public class CommandParser {

	private final Map<String, Set<String>> knownParamsByApi;

	public CommandParser(List<ApiSource> sources) {
		Map<String, Set<String>> m = new HashMap<>();
		for (ApiSource s : sources) {
			m.put(s.id(), s.knownParams());
		}
		this.knownParamsByApi = Map.copyOf(m);
	}

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


	private DataCommandArgs parseData(Map<String, String> params) {
		String api = requireApi(params);            // обязательно
		EntityType entityType = SourceTypeRegistry.of(api);
		Set<String> known = knownParamsByApi.get(api);

		Map<String, String> apiParams = new LinkedHashMap<>(params);
		apiParams.remove("API");

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
				                                  EntityType t = SourceTypeRegistry.of(api);
				                                  if (t == null) {
					                                  throw new IllegalArgumentException(
						                                  "unknown API: '" + api + "'; available: "
							                                  + SourceTypeRegistry.sourceNames());
				                                  }
				                                  return t;
			                                  });
		
		return new ShowCommandArgs(entityType);
	}


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
			throw new IllegalArgumentException("unknown API: '" + api + "'; available: " + knownParamsByApi.keySet());
		}
	}
}