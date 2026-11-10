package dev.malashenko.aggregator.core.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.coinmarketcap.CoinMarketCapHttpApiSource;
import dev.malashenko.aggregator.core.source.coinmarketcap.CoinMarketCapMapper;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsHttpApiSource;
import dev.malashenko.aggregator.core.source.earthquake.usgs.EarthquakeUsgsMapper;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsHttpApiSource;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsMapper;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageFactory;
import dev.malashenko.aggregator.core.storage.StorageFormat;
import dev.malashenko.aggregator.core.storage.WriteMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatorBuilder {
	private final static HttpResponder httpResponder = new HttpResponder();
	private final static ObjectMapper objectMapper= DataStorageFactory.defaultMapper();


	private AggregatorBuilder() {}


	public static Aggregator buildAggregator(List<EntityType> entityTypeList, StorageFormat storageFormat, WriteMode writeMode, String filename) {
		DataStorage dataStorage = DataStorageFactory.getDataStorage(storageFormat, objectMapper);
		Map<EntityType, ApiSource> apiSourceMap = new HashMap<>();
		for (EntityType entityType: entityTypeList) {
			ApiSource apiSource = switch (entityType) {
				case EntityType.NEWS -> new HackerNewsHttpApiSource(httpResponder, new HackerNewsMapper(objectMapper));
				case EntityType.CRYPTOCURRENCY -> new CoinMarketCapHttpApiSource(httpResponder, new CoinMarketCapMapper(objectMapper));
				case EntityType.EARTHQUAKE -> new EarthquakeUsgsHttpApiSource(httpResponder, new EarthquakeUsgsMapper(objectMapper));
			};
			apiSourceMap.put(entityType, apiSource);
		}

		return new Aggregator(dataStorage, writeMode, apiSourceMap, filename);
	}

}


