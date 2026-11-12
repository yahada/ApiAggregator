package dev.malashenko.aggregator.core.source.earthquake.usgs;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceFactory;

public class EarthquakeUsgsSourceFactory implements ApiSourceFactory {

	@Override
	public EntityType getEntityType() {
		return EntityType.EARTHQUAKE;
	}

	@Override
	public ApiSource create(HttpResponder httpResponder, ObjectMapper objectMapper) {
		return new EarthquakeUsgsHttpApiSource(httpResponder, new EarthquakeUsgsMapper(objectMapper));
	}
}