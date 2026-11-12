package dev.malashenko.aggregator.core.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpInfrastructure;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceFactory;
import dev.malashenko.aggregator.core.source.ServiceLoaderApiSourceRegistry;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageFactory;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AggregatorBuilder {

	private final HttpInfrastructure httpInfrastructure;
	private final ObjectMapper objectMapper;
	private final List<ApiSourceFactory> sourceFactories;
	private final OutputPathResolver outputPathResolver;

	public AggregatorBuilder(
		HttpInfrastructure httpInfrastructure,
		ObjectMapper objectMapper,
		List<ApiSourceFactory> sourceFactories,
		OutputPathResolver outputPathResolver
	) {
		this.httpInfrastructure = Objects.requireNonNull(httpInfrastructure);
		this.objectMapper = Objects.requireNonNull(objectMapper);
		this.sourceFactories = List.copyOf(sourceFactories);
		this.outputPathResolver = Objects.requireNonNull(outputPathResolver);
	}

	public static AggregatorBuilder createDefault() {
		HttpInfrastructure httpInfrastructure = HttpInfrastructure.createDefault();

		ServiceLoaderApiSourceRegistry registry = new ServiceLoaderApiSourceRegistry();

		List<ApiSourceFactory> factories = registry.findAll();

		ObjectMapper mapper = ObjectMapperBuilder.buildDefault();

		OutputPathResolver outputPathResolver = new OutputPathResolver();

		return new AggregatorBuilder(httpInfrastructure, mapper, factories, outputPathResolver);
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

		DataStorage dataStorage = DataStorageFactory.getDataStorage(storageFormat, objectMapper);

		Map<EntityType, ApiSource> apiSourceMap = new EnumMap<>(EntityType.class);

		for (EntityType entityType : entityTypeList) {
			ApiSourceFactory factory = sourceFactories.stream()
				                           .filter(item -> (item.getEntityType() == entityType))
				                           .findFirst()
				                           .orElseThrow(() -> new IllegalArgumentException("Unsupported API: " + entityType));

			apiSourceMap.put(entityType, factory.create(httpInfrastructure.httpResponder(), objectMapper));
		}

		Path outputPath = outputPathResolver.resolve(filename, storageFormat);

		return new Aggregator(dataStorage, apiSourceMap, outputPath);
	}
}