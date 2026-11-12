package dev.malashenko.aggregator.core.source.kitsu;

import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KitsuMapperTest {

	private KitsuMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = new KitsuMapper(ObjectMapperBuilder.buildDefault());
	}

	@Test
	void map_parsesValidJson() {
		String json = """
			{"data": [
				{"id": "k1", "attributes": {"updatedAt": "2023-11-14T12:00:00Z", "title": "Test"}}
			]}
			""";

		List<DataRecord> records = mapper.map(json);

		assertEquals(1, records.size());
		DataRecord record = records.get(0);
		assertEquals("kitsu", record.source());
		assertEquals(EntityType.ANIME, record.entityType());
		assertEquals("k1", record.entityId());
		assertTrue(record.observedAt().isPresent());
		assertEquals(Instant.parse("2023-11-14T12:00:00Z"), record.observedAt().orElse(null));
	}

	@Test
	void map_handlesMissingUpdatedAt() {
		String json = """
			{"data": [
				{"id": "k2", "attributes": {"title": "Test"}}
			]}
			""";

		List<DataRecord> records = mapper.map(json);
		assertEquals(1, records.size());
		assertFalse(records.get(0).observedAt().isPresent());
	}

	@Test
	void map_handlesInvalidUpdatedAt() {
		String json = """
			{"data": [
				{"id": "k3", "attributes": {"updatedAt": "not-a-date"}}
			]}
			""";

		List<DataRecord> records = mapper.map(json);
		assertEquals(1, records.size());
		assertFalse(records.get(0).observedAt().isPresent());
	}

	@Test
	void map_returnsEmptyListForNoData() {
		String json = "{\"data\": []}";
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
