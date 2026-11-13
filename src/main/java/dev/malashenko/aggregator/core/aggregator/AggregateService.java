package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;
import java.util.Map;

@Slf4j
public class AggregateService {

	public static void aggregate(DataAggregator aggregator, Map<String, ApiSource> apiSourceMap) {
		if (apiSourceMap == null) {
			throw new IllegalArgumentException("API sources map must not be null");
		}

		for (Map.Entry<String, ApiSource> entry : apiSourceMap.entrySet()) {
			String name = entry.getKey();
			ApiSource source = entry.getValue();

			RequestParams params = source.defaultParams();

			try {
				aggregator.makeRequest(name, params);
				log.info("Successfully aggregated data from {}", name);
			} catch (ApiException e) {
				log.error("API error while polling {}: {}", name, e.getMessage());
			} catch (StorageException e) {
				log.error("Storage error while polling {}: {}", name, e.getMessage());
			} catch (AppException e) {
				log.error("Application error while polling {}: {}", name, e.getMessage());
			} catch (DateTimeParseException e) {
				log.error("Date parse error while polling {}: {}", name, e.getMessage());
			} catch (RuntimeException e) {
				log.error("Unexpected error while polling {}: [{}] {}",
					name, e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}
}
