package dev.malashenko.aggregator.core.source.earthquake.usgs;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.source.AbstractHttpApiSource;

import java.util.Set;

public class EarthquakeUsgsHttpApiSource extends AbstractHttpApiSource {
	private final static String ORIGIN_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
	public final static String SOURCE_ID = "earthquake.usgs";

	public EarthquakeUsgsHttpApiSource(HttpResponder responder, EarthquakeUsgsMapper mapper) {
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
	public Set<String> knownParams() {
		return Set.of("minmagnitude", "maxmagnitude", "limit", "orderby",
			"starttime", "endtime", "latitude", "longitude",
			"maxradiuskm", "eventtype");
	}
}
