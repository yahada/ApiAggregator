package dev.malashenko.aggregator.core.source.coinmarketcap;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;


public class CoinMarketCapHttpApiSource extends AbstractHttpApiSource {

	private final static String ORIGIN_URL = "https://pro-api.coinmarketcap.com/public-api/v2/simple/price";
	final static String SOURCE_ID = "coinmarketcap";

	public CoinMarketCapHttpApiSource(HttpResponder responder, CoinMarketCapMapper mapper) {
		super(responder, mapper);
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}
}
