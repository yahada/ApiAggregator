package dev.malashenko.aggregator.core.source.hackernews;

import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HackerNewsMapperTest {

	private HackerNewsMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = new HackerNewsMapper(ObjectMapperBuilder.buildDefault());
	}

	@Test
	void map_parsesValidJson() {
		String json = """
			{"hits": [
				{"objectID": "hn1", "created_at_i": 1700000000, "title": "Test"}
			]}
			""";

		List<DataRecord> records = mapper.map(json);

		assertEquals(1, records.size());
		DataRecord record = records.get(0);
		assertEquals("hackernews", record.source());
		assertEquals(EntityType.NEWS, record.entityType());
		assertEquals("hn1", record.entityId());
		assertTrue(record.observedAt().isPresent());
		assertEquals(Instant.ofEpochSecond(1700000000), record.observedAt().orElse(null));
	}

	@Test
	void map_handlesMissingTime() {
		String json = """
			{"hits": [
				{"objectID": "hn2"}
			]}
			""";

		List<DataRecord> records = mapper.map(json);
		assertEquals(1, records.size());
		assertFalse(records.get(0).observedAt().isPresent());
	}

	@Test
	void map_returnsEmptyListForNoHits() {
		String json = "{\"hits\": []}";
		List<DataRecord> records = mapper.map(json);
		assertTrue(records.isEmpty());
	}

	@Test
	void map_throwsOnInvalidJson() {
		assertThrows(ApiException.class, () -> mapper.map("not json"));
	}

	@Test
	void map_handlesNullRoot() {
		String json = "{}";
		List<DataRecord> records = mapper.map(json);
		assertTrue(records.isEmpty());
	}
}
