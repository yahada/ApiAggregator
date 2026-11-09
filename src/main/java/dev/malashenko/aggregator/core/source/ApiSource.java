package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;

import java.util.List;

public interface ApiSource {
	List<DataRecord> getApiRequest(RequestParams spec);
}