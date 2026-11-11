package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;

import java.util.List;
import java.util.Set;

public interface ApiSource {
	String id();
	Set<String> knownParams();
	List<DataRecord> getApiRequest(RequestParams spec);

}