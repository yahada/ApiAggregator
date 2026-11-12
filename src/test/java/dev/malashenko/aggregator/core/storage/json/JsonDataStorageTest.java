package dev.malashenko.aggregator.core.storage.json;

import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataStorageTest {

	private JsonDataStorage storage;
	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		storage = new JsonDataStorage(ObjectMapperBuilder.buildDefault());
	}

	private DataRecord makeRecord() {
		return new DataRecord(
			"test",
			EntityType.NEWS,
			"id1",
			Optional.of(Instant.parse("2023-11-14T12:00:00Z")),
			Map.of("key", "value")
		);
	}

	@Test
	void saveData_andReadData_roundTrip() {
		Path file = tempDir.resolve("test.json");
		List<DataRecord> records = List.of(makeRecord());

		storage.saveData(records, file);
		List<DataRecord> read = storage.readData(file);

		assertEquals(1, read.size());
		assertEquals("test", read.get(0).source());
		assertEquals(EntityType.NEWS, read.get(0).entityType());
		assertEquals("id1", read.get(0).entityId());
		assertTrue(read.get(0).observedAt().isPresent());
	}

	@Test
	void saveData_appendsToExisting() {
		Path file = tempDir.resolve("test.json");
		List<DataRecord> records1 = List.of(makeRecord());
		DataRecord record2 = new DataRecord("test2", EntityType.ANIME, "id2", Optional.empty(), Map.of());

		storage.saveData(records1, file);
		storage.saveData(List.of(record2), file);

		List<DataRecord> read = storage.readData(file);
		assertEquals(2, read.size());
	}

	@Test
	void readData_returnsEmptyForNonExistentFile() {
		Path file = tempDir.resolve("missing.json");
		List<DataRecord> read = storage.readData(file);
		assertTrue(read.isEmpty());
	}

	@Test
	void saveData_createsParentDirectories() {
		Path file = tempDir.resolve("sub/dir/test.json");
		List<DataRecord> records = List.of(makeRecord());

		storage.saveData(records, file);
		List<DataRecord> read = storage.readData(file);
		assertEquals(1, read.size());
	}

	@Test
	void saveData_handlesEmptyObservedAt() {
		Path file = tempDir.resolve("test.json");
		DataRecord record = new DataRecord("test", EntityType.NEWS, "id1", Optional.empty(), Map.of());

		storage.saveData(List.of(record), file);
		List<DataRecord> read = storage.readData(file);

		assertEquals(1, read.size());
		assertFalse(read.get(0).observedAt().isPresent());
	}

	@Test
	void readData_throwsOnCorruptedFile() {
		Path file = tempDir.resolve("bad.json");
		try {
			java.nio.file.Files.writeString(file, "not json at all");
		} catch (Exception ignored) {}

		assertThrows(StorageException.class, () -> storage.readData(file));
	}
}
