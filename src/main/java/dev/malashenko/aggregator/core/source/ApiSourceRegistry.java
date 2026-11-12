package dev.malashenko.aggregator.core.source;

import java.util.List;

public interface ApiSourceRegistry {

	List<ApiSourceFactory> findAll();
}
