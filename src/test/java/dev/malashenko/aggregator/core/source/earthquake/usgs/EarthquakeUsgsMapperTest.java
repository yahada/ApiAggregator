package dev.malashenko.aggregator.core.source.earthquake.usgs;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EarthquakeUsgsMapperTest {

	private EarthquakeUsgsMapper mapper;

	@BeforeEach
	void setUp() {
		ObjectMapper om = ObjectMapperBuilder.buildDefault();
		mapper = new EarthquakeUsgsMapper(om);
	}

	@Test
	void map_parsesValidJson() {
		String json = """
			{"features": [
				{"id": "eq1", "properties": {"time": 1700000000000, "mag": 5.0}}
			]}
			""";

		List<DataRecord> records = mapper.map(json);

		assertEquals(1, records.size());
		DataRecord record = records.get(0);
		assertEquals("usgs", record.source());
		assertEquals(EntityType.EARTHQUAKE, record.entityType());
		assertEquals("eq1", record.entityId());
		assertTrue(record.observedAt().isPresent());
		assertEquals(Instant.ofEpochMilli(1700000000000L), record.observedAt().orElse(null));
	}

	@Test
	void map_handlesMissingTime() {
		String json = """
			{"features": [
				{"id": "eq2", "properties": {}}
			]}
			""";

		List<DataRecord> records = mapper.map(json);

		assertEquals(1, records.size());
		assertFalse(records.get(0).observedAt().isPresent());
	}

	@Test
	void map_returnsEmptyListForNoFeatures() {
		String json = "{\"features\": []}";
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
