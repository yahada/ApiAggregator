package dev.malashenko.aggregator.storage;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.model.DataRecord;
import dev.malashenko.aggregator.model.WriteMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonStorage implements DataStorage {
    private final Path path;
    private final ObjectMapper objectMapper;

    public JsonStorage(Path path, ObjectMapper objectMapper) {
        this.path = path;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(List<DataRecord> records, WriteMode mode) throws IOException {
        List<DataRecord> data = new ArrayList<>();

        if (mode == WriteMode.APPEND) {
            data.addAll(readAll());
        }

        data.addAll(records);

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), data);
        log.info("Saved {} records to JSON: {}", data.size(), path);
    }

    @Override
    public List<DataRecord> readAll() throws IOException {
        if (Files.notExists(path) || Files.size(path) == 0) {
            log.debug("JSON file not found or empty: {}", path);
            return List.of();
        }

        List<DataRecord> records = objectMapper.readValue(path.toFile(), new TypeReference<>() {});
        log.info("Read {} records from JSON: {}", records.size(), path);
        return records;
    }
}
