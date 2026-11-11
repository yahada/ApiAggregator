package dev.malashenko.aggregator.core.source.coinmarketcap;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

import java.util.LinkedHashMap;
import java.util.Set;


public class CoinMarketCapHttpApiSource extends AbstractHttpApiSource {

	private final static String ORIGIN_URL = "https://pro-api.coinmarketcap.com/public-api/v2/simple/price";
	public final static String SOURCE_ID = "coinmarketcap";

	public CoinMarketCapHttpApiSource(HttpResponder responder, CoinMarketCapMapper mapper) {
		super(responder, mapper);
	}

	@Override
	public String id() {
		return SOURCE_ID;
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}

	@Override
	public Set<String> knownParams() {
		return Set.of("symbol", "id", "slug", "convert", "skip_invalid");
	}

	@Override
	public RequestParams enrich(RequestParams params) {
		return params;
	}
}
