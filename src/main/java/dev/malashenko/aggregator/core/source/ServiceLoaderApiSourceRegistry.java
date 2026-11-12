package dev.malashenko.aggregator.core.source;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ServiceLoaderApiSourceRegistry implements ApiSourceRegistry {

	@Override
	public List<ApiSourceFactory> findAll() {
		List<ApiSourceFactory> factories = new ArrayList<>();
		ServiceLoader.load(ApiSourceFactory.class).forEach(factories::add);
		return factories;
	}
}
