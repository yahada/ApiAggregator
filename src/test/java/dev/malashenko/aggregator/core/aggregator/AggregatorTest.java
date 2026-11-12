package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.storage.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AggregatorTest {

	@TempDir
	Path tempDir;

	DataStorage dataStorage;
	ApiSource apiSource;
	Aggregator aggregator;

	@BeforeEach
	void setUp() {
		dataStorage = Mockito.mock(DataStorage.class);
		apiSource = Mockito.mock(ApiSource.class);
		when(apiSource.id()).thenReturn("test");
		when(apiSource.knownParams()).thenReturn(Set.of());

		aggregator = new Aggregator(dataStorage, Map.of(EntityType.NEWS, apiSource), tempDir.resolve("out.json"));
	}

	@Test
	void makeRequest_savesData() {
		DataRecord record = new DataRecord("test", EntityType.NEWS, "1", java.util.Optional.empty(), Map.of());
		when(apiSource.getApiRequest(any())).thenReturn(List.of(record));

		aggregator.makeRequest(EntityType.NEWS, new RequestParams(Map.of()));

		verify(apiSource).getApiRequest(any());
		verify(dataStorage).saveData(eq(List.of(record)), any(Path.class));
	}

	@Test
	void makeRequest_throwsWhenSourceNotConfigured() {
		assertThrows(IllegalArgumentException.class, () ->
			aggregator.makeRequest(EntityType.ANIME, new RequestParams(Map.of()))
		);
	}

	@Test
	void readData_returnsAllWhenTypeIsNull() {
		DataRecord record = new DataRecord("test", EntityType.NEWS, "1", java.util.Optional.empty(), Map.of());
		when(dataStorage.readData(any(Path.class))).thenReturn(List.of(record));

		List<DataRecord> result = aggregator.readData(null);

		assertEquals(1, result.size());
		verify(dataStorage).readData(any(Path.class));
	}

	@Test
	void readData_filtersByType() {
		DataRecord news = new DataRecord("test", EntityType.NEWS, "1", java.util.Optional.empty(), Map.of());
		DataRecord anime = new DataRecord("test", EntityType.ANIME, "2", java.util.Optional.empty(), Map.of());
		when(dataStorage.readData(any(Path.class))).thenReturn(List.of(news, anime));

		List<DataRecord> result = aggregator.readData(EntityType.NEWS);

		assertEquals(1, result.size());
		assertEquals(EntityType.NEWS, result.get(0).entityType());
	}

	@Test
	void getApiSources_returnsCopy() {
		List<ApiSource> sources = aggregator.getApiSources();
		assertEquals(1, sources.size());
		assertThrows(UnsupportedOperationException.class, () -> sources.add(null));
	}

	@Test
	void constructor_throwsOnNullStorage() {
		assertThrows(NullPointerException.class, () ->
			new Aggregator(null, Map.of(), tempDir.resolve("out.json"))
		);
	}

	@Test
	void constructor_throwsOnNullOutputPath() {
		assertThrows(NullPointerException.class, () ->
			new Aggregator(dataStorage, Map.of(), null)
		);
	}
}
