package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.InitialArgs;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InitialArgsParserTest {

	private final InitialArgsParser parser = new InitialArgsParser();

	@Test
	void getArgs_parsesValidArgs() {
		String[] args = {"sources=usgs,hackernews", "format=json", "mode=new", "file=output.json"};
		InitialArgs result = parser.getArgs(args);

		assertEquals(2, result.apiList().size());
		assertTrue(result.apiList().contains(EntityType.EARTHQUAKE));
		assertTrue(result.apiList().contains(EntityType.NEWS));
		assertEquals(StorageFormat.JSON, result.format());
		assertEquals(WriteMode.NEW, result.writeMode());
		assertEquals("output.json", result.filename());
	}

	@Test
	void getArgs_parsesCsvFormat() {
		String[] args = {"sources=kitsu", "format=csv", "mode=append"};
		InitialArgs result = parser.getArgs(args);

		assertEquals(StorageFormat.CSV, result.format());
		assertEquals(WriteMode.APPEND, result.writeMode());
	}

	@Test
	void getArgs_parsesWithDoubleDashes() {
		String[] args = {"--sources=usgs", "--format=json", "--mode=new"};
		InitialArgs result = parser.getArgs(args);

		assertEquals(1, result.apiList().size());
		assertEquals(EntityType.EARTHQUAKE, result.apiList().getFirst());
	}

	@Test
	void getArgs_throwsOnMissingSources() {
		String[] args = {"format=json", "mode=new"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnMissingFormat() {
		String[] args = {"sources=usgs", "mode=new"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnMissingMode() {
		String[] args = {"sources=usgs", "format=json"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnUnknownSource() {
		String[] args = {"sources=unknown", "format=json", "mode=new"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnUnknownFormat() {
		String[] args = {"sources=usgs", "format=xml", "mode=new"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnUnknownMode() {
		String[] args = {"sources=usgs", "format=json", "mode=overwrite"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void getArgs_throwsOnBadArgumentFormat() {
		String[] args = {"sources=usgs", "format=json", "mode"};
		assertThrows(IllegalArgumentException.class, () -> parser.getArgs(args));
	}

	@Test
	void parseNameValue_handlesEmptyArray() {
		Map<String, String> result = InitialArgsParser.parseNameValue(new String[0]);
		assertTrue(result.isEmpty());
	}

	@Test
	void parseEntityTypes_singleSource() {
		List<EntityType> types = InitialArgsParser.parseEntityTypes("usgs");
		assertEquals(1, types.size());
		assertEquals(EntityType.EARTHQUAKE, types.getFirst());
	}

	@Test
	void parseEntityTypes_multipleSources() {
		List<EntityType> types = InitialArgsParser.parseEntityTypes("usgs, kitsu, hackernews");
		assertEquals(3, types.size());
	}

	@Test
	void toEntityType_caseInsensitive() {
		assertEquals(EntityType.ANIME, InitialArgsParser.toEntityType("KITSU"));
		assertEquals(EntityType.ANIME, InitialArgsParser.toEntityType("kitsu"));
	}

	@Test
	void parseStorageFormat_caseInsensitive() {
		assertEquals(StorageFormat.JSON, InitialArgsParser.parseStorageFormat("JSON"));
		assertEquals(StorageFormat.CSV, InitialArgsParser.parseStorageFormat("csv"));
	}

	@Test
	void parseWriteMode_caseInsensitive() {
		assertEquals(WriteMode.NEW, InitialArgsParser.parseWriteMode("NEW"));
		assertEquals(WriteMode.APPEND, InitialArgsParser.parseWriteMode("append"));
	}
}
