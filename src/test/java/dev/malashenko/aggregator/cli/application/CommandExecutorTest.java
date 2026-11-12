package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.cli.parsers.CommandParser;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
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
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class CommandExecutorTest {

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		// No setup needed — we create mocks per test
	}

	@Test
	void execute_dataCommand_callsAggregator() {
		DataStorage dataStorage = Mockito.mock(DataStorage.class);
		ApiSource apiSource = Mockito.mock(ApiSource.class);
		when(apiSource.id()).thenReturn("usgs");
		when(apiSource.knownParams()).thenReturn(Set.of("limit"));
		when(apiSource.getApiRequest(any())).thenReturn(List.of());

		Aggregator aggregator = new Aggregator(dataStorage, Map.of(EntityType.EARTHQUAKE, apiSource), tempDir.resolve("out.json"));

		CommandParser parser = new CommandParser(Map.of("usgs", Set.of("limit")));
		CommandExecutor executor = new CommandExecutor(parser, aggregator);

		// This should not throw
		executor.execute("data API=usgs limit=10");

		verify(apiSource).getApiRequest(any());
		verify(dataStorage).saveData(anyList(), any(Path.class));
	}

	@Test
	void execute_showCommand_readsData() {
		DataStorage dataStorage = Mockito.mock(DataStorage.class);
		ApiSource apiSource = Mockito.mock(ApiSource.class);
		when(apiSource.id()).thenReturn("usgs");
		when(apiSource.knownParams()).thenReturn(Set.of());

		DataRecord record = new DataRecord("test", EntityType.NEWS, "1", Optional.empty(), Map.of());
		when(dataStorage.readData(any(Path.class))).thenReturn(List.of(record));

		Aggregator aggregator = new Aggregator(dataStorage, Map.of(EntityType.EARTHQUAKE, apiSource), tempDir.resolve("out.json"));

		CommandParser parser = new CommandParser(Map.of("usgs", Set.of()));
		CommandExecutor executor = new CommandExecutor(parser, aggregator);

		executor.execute("show");

		verify(dataStorage).readData(any(Path.class));
	}

	@Test
	void execute_showCommand_withApiFilter() {
		DataStorage dataStorage = Mockito.mock(DataStorage.class);
		ApiSource apiSource = Mockito.mock(ApiSource.class);
		when(apiSource.id()).thenReturn("usgs");
		when(apiSource.knownParams()).thenReturn(Set.of());

		DataRecord record = new DataRecord("test", EntityType.EARTHQUAKE, "1", Optional.empty(), Map.of());
		when(dataStorage.readData(any(Path.class))).thenReturn(List.of(record));

		Aggregator aggregator = new Aggregator(dataStorage, Map.of(EntityType.EARTHQUAKE, apiSource), tempDir.resolve("out.json"));

		CommandParser parser = new CommandParser(Map.of("usgs", Set.of()));
		CommandExecutor executor = new CommandExecutor(parser, aggregator);

		executor.execute("show API=usgs");

		verify(dataStorage).readData(any(Path.class));
	}

	@Test
	void execute_unknownCommand_doesNotThrow() {
		DataStorage dataStorage = Mockito.mock(DataStorage.class);
		ApiSource apiSource = Mockito.mock(ApiSource.class);
		when(apiSource.id()).thenReturn("usgs");
		when(apiSource.knownParams()).thenReturn(Set.of());

		Aggregator aggregator = new Aggregator(dataStorage, Map.of(EntityType.EARTHQUAKE, apiSource), tempDir.resolve("out.json"));

		CommandParser parser = new CommandParser(Map.of("usgs", Set.of()));
		CommandExecutor executor = new CommandExecutor(parser, aggregator);

		// Should not throw — exceptions are caught internally
		executor.execute("unknown command");
	}
}
