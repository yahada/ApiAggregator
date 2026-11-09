package dev.malashenko.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsHttpApiSource;
import dev.malashenko.aggregator.core.source.hackernews.HackerNewsMapper;

import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		HackerNewsMapper marketCapMapper = new HackerNewsMapper(new ObjectMapper());
		HttpResponder httpResponder = new HttpResponder();
		HackerNewsHttpApiSource apiSource = new HackerNewsHttpApiSource(httpResponder, marketCapMapper);
		RequestParams params = new RequestParams(Map.of(
			"query", "java",
			"tags", "story",
			"hitsPerPage", "20",
			"numericFilters", "points>50"
		));
		List<DataRecord> recordList = apiSource.getApiRequest(params);


		for (DataRecord record: recordList) {
			System.out.println(record);
		}

	}
}
