package dev.malashenko.aggregator.core.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class DataStorageRegistry {

	public static List<DataStorageProvider> findAll() {
		List<DataStorageProvider> providers = new ArrayList<>();
		ServiceLoader.load(DataStorageProvider.class).forEach(providers::add);
		return providers;
	}
}
