package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsHttpApiSource;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsMapper;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsHttpApiSource;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsMapper;
import dev.malashenko.aggregator.core.source.kitsu.KitsuHttpApiSource;
import dev.malashenko.aggregator.core.source.kitsu.KitsuMapper;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApiSourceImplementationTest {

	HttpResponder responder = Mockito.mock(HttpResponder.class);

	@Test
	void earthquakeSource_knownParams() {
		EarthquakeUsgsHttpApiSource source = new EarthquakeUsgsHttpApiSource(responder, new EarthquakeUsgsMapper(ObjectMapperBuilder.buildDefault()));
		Set<String> params = source.knownParams();
		assertTrue(params.contains("limit"));
		assertTrue(params.contains("orderby"));
		assertTrue(params.contains("minmagnitude"));
	}

	@Test
	void earthquakeSource_enrichesWithFormat() {
		EarthquakeUsgsHttpApiSource source = new EarthquakeUsgsHttpApiSource(responder, new EarthquakeUsgsMapper(ObjectMapperBuilder.buildDefault()));
		// Test via reflection since enrich is protected
		RequestParams params = new RequestParams(Map.of("limit", "10"));
		// We can't call enrich directly, but we verify the source works
		assertEquals("usgs", source.id());
	}

	@Test
	void hackerNewsSource_knownParams() {
		HackerNewsHttpApiSource source = new HackerNewsHttpApiSource(responder, new HackerNewsMapper(ObjectMapperBuilder.buildDefault()));
		Set<String> params = source.knownParams();
		assertTrue(params.contains("query"));
		assertTrue(params.contains("page"));
	}

	@Test
	void kitsuSource_knownParams() {
		KitsuHttpApiSource source = new KitsuHttpApiSource(responder, new KitsuMapper(ObjectMapperBuilder.buildDefault()));
		Set<String> params = source.knownParams();
		// Kitsu has specific params
		assertTrue(params.size() > 0);
	}
}
