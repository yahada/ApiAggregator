package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.storage.StorageFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OutputPathResolverTest {

	private final OutputPathResolver resolver = new OutputPathResolver();
	@TempDir
	Path tempDir;

	@Test
	void resolve_returnsDefaultForNullFilename() {
		Path path = resolver.resolve(null, StorageFormat.JSON);
		assertEquals("data.json", path.toString());
	}

	@Test
	void resolve_returnsDefaultForBlankFilename() {
		Path path = resolver.resolve("  ", StorageFormat.CSV);
		assertEquals("data.csv", path.toString());
	}

	@Test
	void resolve_addsExtensionWhenMissing() {
		Path path = resolver.resolve("output", StorageFormat.JSON);
		assertEquals("output.json", path.toString());
	}

	@Test
	void resolve_returnsPathWithCorrectExtension() {
		Path path = resolver.resolve("output.json", StorageFormat.JSON);
		assertEquals("output.json", path.toString());
	}

	@Test
	void resolve_throwsOnMismatchedExtension() {
		assertThrows(IllegalArgumentException.class,
			() -> resolver.resolve("output.csv", StorageFormat.JSON));
	}

	@Test
	void resolve_isCaseInsensitiveForExtension() {
		Path path = resolver.resolve("output.JSON", StorageFormat.JSON);
		assertEquals("output.JSON", path.toString());
	}
}
