package dev.malashenko.aggregator.core.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsSourceFactory;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsSourceFactory;
import dev.malashenko.aggregator.core.source.kitsu.KitsuSourceFactory;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ApiSourceFactoryTest {

	ObjectMapper om = ObjectMapperBuilder.buildDefault();
	HttpResponder responder = Mockito.mock(HttpResponder.class);

	@Test
	void earthquakeFactory_createsSource() {
		EarthquakeUsgsSourceFactory factory = new EarthquakeUsgsSourceFactory();
		assertEquals(EntityType.EARTHQUAKE, factory.getEntityType());

		ApiSource source = factory.create(responder, om);
		assertNotNull(source);
		assertEquals("usgs", source.id());
	}

	@Test
	void hackerNewsFactory_createsSource() {
		HackerNewsSourceFactory factory = new HackerNewsSourceFactory();
		assertEquals(EntityType.NEWS, factory.getEntityType());

		ApiSource source = factory.create(responder, om);
		assertNotNull(source);
		assertEquals("hackernews", source.id());
	}

	@Test
	void kitsuFactory_createsSource() {
		KitsuSourceFactory factory = new KitsuSourceFactory();
		assertEquals(EntityType.ANIME, factory.getEntityType());

		ApiSource source = factory.create(responder, om);
		assertNotNull(source);
		assertEquals("kitsu", source.id());
	}
}
