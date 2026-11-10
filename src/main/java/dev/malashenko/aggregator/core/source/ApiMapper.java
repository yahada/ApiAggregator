package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.util.List;

public interface ApiMapper {
	public List<DataRecord> map(String json);

}
