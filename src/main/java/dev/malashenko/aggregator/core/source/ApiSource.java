package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;

import java.util.List;

public interface ApiSource {
	RequestParams defaultParams();
	List<DataRecord> getApiRequest(RequestParams spec);
	List<DataRecord> map(String json);
}