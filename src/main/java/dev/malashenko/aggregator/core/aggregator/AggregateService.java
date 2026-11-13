package dev.malashenko.aggregator.core.aggregator;

import dev.malashenko.aggregator.core.exception.ApiException;
import dev.malashenko.aggregator.core.exception.AppException;
import dev.malashenko.aggregator.core.exception.StorageException;
import dev.malashenko.aggregator.core.model.EntityType;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.ApiSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;
import java.util.Map;

public class AggregateService {

	private static final Logger logger = LoggerFactory.getLogger(AggregateService.class);

	private final Aggregator aggregator;

	public AggregateService(Aggregator aggregator) {
		if (aggregator == null) {
			throw new IllegalArgumentException("Aggregator must not be null");
		}
		this.aggregator = aggregator;
	}

	public void aggregate(Map<EntityType, ApiSource> apiSourceMap) {
		if (apiSourceMap == null) {
			throw new IllegalArgumentException("API sources map must not be null");
		}

		for (Map.Entry<EntityType, ApiSource> entry : apiSourceMap.entrySet()) {
			EntityType type = entry.getKey();
			ApiSource source = entry.getValue();

			RequestParams params = source.defaultParams();

			try {
				aggregator.makeRequest(type, params);
				logger.info("Successfully aggregated data from {}", type);
			} catch (ApiException e) {
				logger.error("API error while polling {}: {}", type, e.getMessage());
			} catch (StorageException e) {
				logger.error("Storage error while polling {}: {}", type, e.getMessage());
			} catch (AppException e) {
				logger.error("Application error while polling {}: {}", type, e.getMessage());
			} catch (DateTimeParseException e) {
				logger.error("Date parse error while polling {}: {}", type, e.getMessage());
			} catch (RuntimeException e) {
				logger.error("Unexpected error while polling {}: [{}] {}",
					type, e.getClass().getSimpleName(), e.getMessage());
			}
		}
	}
}
