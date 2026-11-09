package dev.malashenko.aggregator.core.source.earthquake.usgs;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

public class EarthquakeUsgsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
	final static String SOURCE_ID = "earthquake.usgs";

	public EarthquakeUsgsHttpApiSource(HttpResponder responder, EarthquakeUsgsMapper mapper) {
		super(responder, mapper);
	}

	@Override
	protected String originUrl() {
		return ORIGIN_URL;
	}
}
