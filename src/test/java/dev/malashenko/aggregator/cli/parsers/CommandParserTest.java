package dev.malashenko.aggregator.cli.parsers;

import dev.malashenko.aggregator.cli.args.CommandArgs;
import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.model.EntityType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

	private CommandParser createParser() {
		return new CommandParser(Map.of(
			"usgs", Set.of("limit", "orderby", "minmagnitude"),
			"hackernews", Set.of("query", "page"),
			"kitsu", Set.of("filter[slug]", "page")
		));
	}

	@Test
	void parse_dataCommand_withValidParams() {
		CommandParser parser = createParser();
		CommandArgs args = parser.parse("data API=usgs limit=10");

		assertInstanceOf(DataCommandArgs.class, args);
		DataCommandArgs data = (DataCommandArgs) args;
		assertEquals("usgs", data.apiName());
		assertEquals(EntityType.EARTHQUAKE, data.entityType());
		assertEquals("10", data.apiParams().queryParams().get("limit"));
	}

	@Test
	void parse_dataCommand_missingApi() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse("data limit=10"));
	}

	@Test
	void parse_dataCommand_unknownApi() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse("data API=unknown"));
	}

	@Test
	void parse_dataCommand_unknownParam() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse("data API=usgs unknown=1"));
	}

	@Test
	void parse_showCommand_noParams() {
		CommandParser parser = createParser();
		CommandArgs args = parser.parse("show");

		assertInstanceOf(ShowCommandArgs.class, args);
		ShowCommandArgs show = (ShowCommandArgs) args;
		assertFalse(show.optional().isPresent());
	}

	@Test
	void parse_showCommand_withApi() {
		CommandParser parser = createParser();
		CommandArgs args = parser.parse("show API=kitsu");

		assertInstanceOf(ShowCommandArgs.class, args);
		ShowCommandArgs show = (ShowCommandArgs) args;
		assertTrue(show.optional().isPresent());
		assertEquals(EntityType.ANIME, show.optional().orElse(null));
	}

	@Test
	void parse_showCommand_withUnknownParam() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse("show unknown=1"));
	}

	@Test
	void parse_unknownCommand() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse("unknown"));
	}

	@Test
	void parse_emptyCommand() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
	}

	@Test
	void parse_nullCommand() {
		CommandParser parser = createParser();
		assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
	}

	@Test
	void parse_dataCommand_withMultipleParams() {
		CommandParser parser = createParser();
		CommandArgs args = parser.parse("data API=usgs limit=10 orderby=time");

		assertInstanceOf(DataCommandArgs.class, args);
		DataCommandArgs data = (DataCommandArgs) args;
		assertEquals("10", data.apiParams().queryParams().get("limit"));
		assertEquals("time", data.apiParams().queryParams().get("orderby"));
	}
}
