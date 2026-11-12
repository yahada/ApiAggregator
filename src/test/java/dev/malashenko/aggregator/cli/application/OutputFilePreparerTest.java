package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.InitialArgs;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutputFilePreparerTest {

	@TempDir
	Path tempDir;

	@Test
	void prepare_newMode_createsEmptyFile() throws IOException {
		Path file = tempDir.resolve("test.json");
		InitialArgs args = new InitialArgs(List.of(EntityType.NEWS), StorageFormat.JSON, WriteMode.NEW, file.toString());

		new OutputFilePreparer().prepare(args);

		assertTrue(java.nio.file.Files.exists(file));
		assertEquals(0, java.nio.file.Files.size(file));
	}

	@Test
	void prepare_newMode_truncatesExistingFile() throws IOException {
		Path file = tempDir.resolve("test.json");
		try {
			java.nio.file.Files.writeString(file, "old data");
		} catch (Exception ignored) {}

		InitialArgs args = new InitialArgs(List.of(EntityType.NEWS), StorageFormat.JSON, WriteMode.NEW, file.toString());
		new OutputFilePreparer().prepare(args);

		assertEquals(0, java.nio.file.Files.size(file));
	}

	@Test
	void prepare_appendMode_createsNewFile() {
		Path file = tempDir.resolve("new_append.json");
		InitialArgs args = new InitialArgs(List.of(EntityType.NEWS), StorageFormat.JSON, WriteMode.APPEND, file.toString());

		new OutputFilePreparer().prepare(args);

		assertTrue(java.nio.file.Files.exists(file));
	}

	@Test
	void prepare_appendMode_keepsExistingFile() throws IOException {
		Path file = tempDir.resolve("existing.json");
		try {
			java.nio.file.Files.writeString(file, "existing data");
		} catch (Exception ignored) {}

		InitialArgs args = new InitialArgs(List.of(EntityType.NEWS), StorageFormat.JSON, WriteMode.APPEND, file.toString());
		new OutputFilePreparer().prepare(args);

		assertEquals("existing data", java.nio.file.Files.readString(file));
	}

	@Test
	void prepare_createsParentDirectories() {
		Path file = tempDir.resolve("sub/dir/test.json");
		InitialArgs args = new InitialArgs(List.of(EntityType.NEWS), StorageFormat.JSON, WriteMode.NEW, file.toString());

		new OutputFilePreparer().prepare(args);

		assertTrue(java.nio.file.Files.exists(file));
	}
}
