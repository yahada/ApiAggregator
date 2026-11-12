package dev.malashenko.aggregator.core.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.*;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.kitsu.*;
import dev.malashenko.aggregator.core.source.earthquake.usgs.*;
import dev.malashenko.aggregator.core.source.hackernews.*;
import dev.malashenko.aggregator.core.storage.*;
import dev.malashenko.aggregator.core.tools.FileExtension;

import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AggregatorBuilder {

	private final HttpResponder httpResponder;
	private final ObjectMapper objectMapper;

	public AggregatorBuilder(
		HttpResponder httpResponder,
		ObjectMapper objectMapper
	) {
		this.httpResponder = Objects.requireNonNull(httpResponder);
		this.objectMapper = Objects.requireNonNull(objectMapper);
	}

	public static AggregatorBuilder createDefault() {
		HttpClientProvider clientProvider = new HttpClientProvider();

		HttpRequestFactory requestFactory = new HttpRequestFactory();

		RetryPolicy retryPolicy = new RetryPolicy(
			3,
			Duration.ofSeconds(1),
			Duration.ofSeconds(10)
		);

		HttpResponder responder = new HttpResponder(
			clientProvider,
			requestFactory,
			retryPolicy
		);

		ObjectMapper mapper = DataStorageFactory.defaultMapper();

		return new AggregatorBuilder(responder, mapper);
	}

	public Aggregator buildAggregator(
		List<EntityType> entityTypeList,
		StorageFormat storageFormat,
		WriteMode writeMode,
		String filename
	) {
		Objects.requireNonNull(entityTypeList);
		Objects.requireNonNull(storageFormat);
		Objects.requireNonNull(writeMode);

		DataStorage dataStorage =
			DataStorageFactory.getDataStorage(storageFormat, objectMapper);

		Map<EntityType, ApiSource> apiSourceMap =
			new EnumMap<>(EntityType.class);

		for (EntityType entityType : entityTypeList) {
			ApiSource apiSource = switch (entityType) {
				case NEWS -> new HackerNewsHttpApiSource(
					httpResponder,
					new HackerNewsMapper(objectMapper)
				);

				case ANIME -> new KitsuHttpApiSource(
					httpResponder,
					new KitsuMapper(objectMapper)
				);

				case EARTHQUAKE -> new EarthquakeUsgsHttpApiSource(
					httpResponder,
					new EarthquakeUsgsMapper(objectMapper)
				);
			};

			apiSourceMap.put(entityType, apiSource);
		}

		Path outputPath = resolveOutputPath(filename, storageFormat);

		return new Aggregator(
			dataStorage,
			writeMode,
			apiSourceMap,
			outputPath
		);
	}

	private Path resolveOutputPath(
		String filename,
		StorageFormat storageFormat
	) {
		String extension = FileExtension.formatOf(storageFormat);

		if (filename == null || filename.isBlank()) {
			return Path.of("data." + extension);
		}

		if (FileExtension.extension(filename).isEmpty()) {
			return Path.of(filename + "." + extension);
		}

		return Path.of(filename);
	}
}