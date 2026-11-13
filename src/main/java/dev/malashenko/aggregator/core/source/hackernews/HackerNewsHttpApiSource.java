package dev.malashenko.aggregator.core.source.hackernews;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

import java.util.Map;
import java.util.Set;

public class HackerNewsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://hn.algolia.com/api/v1/search";
	public final static String SOURCE_ID = "hackernews";

	public HackerNewsHttpApiSource(HttpResponder responder, HackerNewsMapper mapper) {
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
		return new RequestParams(Map.of("query", "java", "hitsPerPage", "5"));
	}

	@Override
	protected RequestParams enrich(RequestParams params) {
		return params;
	}
}
