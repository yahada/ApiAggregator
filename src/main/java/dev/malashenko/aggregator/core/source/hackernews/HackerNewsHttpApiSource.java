package dev.malashenko.aggregator.core.source.hackernews;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

public class HackerNewsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://hn.algolia.com/api/v1/search";
	static final String SOURCE_ID = "hackernews";

	public HackerNewsHttpApiSource(HttpResponder responder, HackerNewsMapper mapper) {
		super(responder, mapper);
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}
}
