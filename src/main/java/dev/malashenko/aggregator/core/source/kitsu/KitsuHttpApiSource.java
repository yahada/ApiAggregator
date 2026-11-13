package dev.malashenko.aggregator.core.source.kitsu;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class KitsuHttpApiSource extends AbstractHttpApiSource {

	public static final String SOURCE_ID = "kitsu";
	private static final String ORIGIN_URL = "https://kitsu.io/api/edge/anime";

	public KitsuHttpApiSource(HttpResponder responder, KitsuMapper mapper) {
		super(responder, mapper);
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}

	@Override
	public String id() {
		return SOURCE_ID;
	}

	@Override
	public RequestParams defaultParams() {
		return new RequestParams(Map.of("page[limit]", "5"));
	}

	@Override
	protected RequestParams enrich(RequestParams params) {
		Map<String, String> m = new LinkedHashMap<>(params.queryParams());
		m.putIfAbsent("page[limit]", "10");
		return new RequestParams(m);
	}
}