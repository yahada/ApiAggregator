package dev.malashenko.aggregator.core.tools;

import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UrlMakerTest {

	@Test
	void makeUrl_returnsOriginWhenNoParams() {
		String url = UrlMaker.makeUrl("https://example.com", new RequestParams(Map.of()));
		assertEquals("https://example.com", url);
	}

	@Test
	void makeUrl_appendsParamsWithQuestionMark() {
		RequestParams params = new RequestParams(Map.of("key", "value"));
		String url = UrlMaker.makeUrl("https://example.com", params);
		assertEquals("https://example.com?key=value", url);
	}

	@Test
	void makeUrl_appendsParamsWithAmpersand() {
		RequestParams params = new RequestParams(Map.of("key", "value"));
		String url = UrlMaker.makeUrl("https://example.com?existing=1", params);
		assertEquals("https://example.com?existing=1&key=value", url);
	}

	@Test
	void makeUrl_encodesSpecialCharacters() {
		RequestParams params = new RequestParams(Map.of("q", "hello world"));
		String url = UrlMaker.makeUrl("https://example.com", params);
		// URLEncoder encodes space as '+' in application/x-www-form-urlencoded
		assertTrue(url.contains("q=hello+world") || url.contains("q=hello%20world"));
	}

	@Test
	void makeUrl_handlesMultipleParams() {
		RequestParams params = new RequestParams(Map.of("a", "1", "b", "2"));
		String url = UrlMaker.makeUrl("https://example.com", params);
		assertTrue(url.contains("a=1"));
		assertTrue(url.contains("b=2"));
	}
}

class FileExtensionTest {

	@Test
	void extension_returnsCorrectExtension() {
		assertEquals("json", FileExtension.extension("file.json"));
		assertEquals("csv", FileExtension.extension("file.csv"));
	}

	@Test
	void extension_returnsEmptyForNoExtension() {
		assertEquals("", FileExtension.extension("file"));
	}

	@Test
	void extension_returnsEmptyForDotOnly() {
		assertEquals("", FileExtension.extension("file."));
	}

	@Test
	void formatOf_string_returnsCorrectFormat() {
		assertEquals(StorageFormat.JSON, FileExtension.formatOf("file.json"));
		assertEquals(StorageFormat.CSV, FileExtension.formatOf("file.csv"));
		assertNull(FileExtension.formatOf("file.txt"));
	}

	@Test
	void formatOf_enum_returnsCorrectExtension() {
		assertEquals("json", FileExtension.formatOf(StorageFormat.JSON));
		assertEquals("csv", FileExtension.formatOf(StorageFormat.CSV));
	}
}

class SourceTypeRegistryTest {

	@Test
	void of_returnsCorrectEntityType() {
		assertEquals(dev.malashenko.aggregator.core.model.EntityType.ANIME,
			dev.malashenko.aggregator.core.model.SourceTypeRegistry.of("kitsu"));
		assertEquals(dev.malashenko.aggregator.core.model.EntityType.EARTHQUAKE,
			dev.malashenko.aggregator.core.model.SourceTypeRegistry.of("usgs"));
		assertEquals(dev.malashenko.aggregator.core.model.EntityType.NEWS,
			dev.malashenko.aggregator.core.model.SourceTypeRegistry.of("hackernews"));
	}

	@Test
	void of_returnsNullForUnknown() {
		assertNull(dev.malashenko.aggregator.core.model.SourceTypeRegistry.of("unknown"));
	}

	@Test
	void of_returnsNullForNull() {
		assertNull(dev.malashenko.aggregator.core.model.SourceTypeRegistry.of(null));
	}

	@Test
	void of_isCaseInsensitive() {
		assertEquals(dev.malashenko.aggregator.core.model.EntityType.ANIME,
			dev.malashenko.aggregator.core.model.SourceTypeRegistry.of("KITSU"));
	}

	@Test
	void sourceNames_returnsAllSources() {
		var names = dev.malashenko.aggregator.core.model.SourceTypeRegistry.sourceNames();
		assertEquals(3, names.size());
		assertTrue(names.contains("kitsu"));
		assertTrue(names.contains("usgs"));
		assertTrue(names.contains("hackernews"));
	}
}
