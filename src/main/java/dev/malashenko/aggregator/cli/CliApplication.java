package dev.malashenko.aggregator.cli;

import dev.malashenko.aggregator.factory.AppFactory;
import lombok.extern.slf4j.Slf4j;

import dev.malashenko.aggregator.aggregator.Aggregator;
import dev.malashenko.aggregator.model.DataRecord;
import dev.malashenko.aggregator.model.WriteMode;
import dev.malashenko.aggregator.storage.DataStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class CliApplication {
	private final Aggregator aggregator;
	private final AppFactory appFactory;

	public CliApplication(Aggregator aggregator, AppFactory appFactory) {
		this.aggregator = aggregator;
		this.appFactory = appFactory;
	}

	public void run(String[] args) throws IOException, InterruptedException {
		if (args.length == 0) {
			throw new IllegalArgumentException("Command is required: data or show");
		}

		String command = args[0].toLowerCase(Locale.ROOT);

		Map<String, String> params = parseParams(Arrays.copyOfRange(args, 1, args.length));

		switch (command) {
			case "data" -> runData(params);
			case "show" -> runShow(params);
			default -> throw new IllegalArgumentException("Unknown command: " + command);
		}
	}

	private void runData(Map<String, String> params) throws IOException, InterruptedException {
		validateUnknownParams(params, List.of("sources", "format", "mode", "file")); //HashSet надобы

		List<String> sources = parseSources(params);

		String format = getRequired(params, "format");
		String mode = getRequired(params, "mode");
		String file = getRequired(params, "file");

		Path filePath = Path.of(file);
		validateFileFormat(format, filePath);

		WriteMode writeMode = parseWriteMode(mode);
		DataStorage storage = appFactory.createStorage(format, filePath);
		List<DataRecord> records = aggregator.aggregate(sources);

		storage.save(records, writeMode);

		log.info("Data command completed: {} records saved", records.size());
	}

	private void runShow(Map<String, String> params) throws IOException {
		validateUnknownParams(params, List.of("file", "format", "source")); //HashSet надобы

		String file = getRequired(params, "file");
		String format = getRequired(params, "format");
		String source = params.get("source");

		Path filePath = Path.of(file);
		validateFileFormat(format, filePath);

		DataStorage storage = appFactory.createStorage(format, filePath);
		List<DataRecord> records = storage.readBySource(source);

		RecordPrinter.printAll(records);
		log.info("Total records printed: {}", records.size());
	}

	private Map<String, String> parseParams(String[] args) {
		Map<String, String> params = new HashMap<>();
		for (String arg : args) {
			int eq = arg.indexOf('=');
			if (eq == -1) {
				throw new IllegalArgumentException("Invalid argument: " + arg + " (expected key=value)");
			}
			params.put(arg.substring(0, eq), arg.substring(eq + 1));
		}
		return params;
	}

	private List<String> parseSources(Map<String, String> params) {
		String sources = getRequired(params, "sources");
		return Arrays.asList(sources.split(","));
	}

	public WriteMode parseWriteMode(String mode) {
		return switch (mode.toLowerCase(Locale.ROOT)) {
			case "new" -> WriteMode.NEW;
			case "append" -> WriteMode.APPEND;
			default -> throw new IllegalArgumentException("Unsupported mode: " + mode);
		};
	}

	private String getRequired(Map<String, String> params, String key) {
		String value = params.get(key);
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Missing required parameter: " + key);
		}
		return value;
	}

	private void validateUnknownParams(Map<String, String> params, List<String> allowed) {
		for (String key : params.keySet()) {
			if (!allowed.contains(key)) {
				throw new IllegalArgumentException("Unknown parameter: " + key);
			}
		}
	}

	private void validateFileFormat(String format, Path filePath) {
		String extension = getFileExtension(filePath);
		String expectedExtension = format.toLowerCase(Locale.ROOT);

		if (!extension.equals(expectedExtension)) {
			throw new IllegalArgumentException("File format mismatch: format=" + format + "but file extension=." + extension);
		}
	}

	private String getFileExtension(Path path) {
		String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
		int lastDot = fileName.lastIndexOf('.');

		if (lastDot == -1) {
			throw new IllegalArgumentException("File has no extension: " + path);
		}
		return fileName.substring(lastDot + 1);
	}
}
