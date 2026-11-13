package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.tools.UrlMaker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractHttpApiSource implements ApiSource {
	private final HttpResponder responder;

	protected AbstractHttpApiSource(HttpResponder responder) {
		this.responder = responder;
	}

	protected abstract String originUrl();

	protected abstract RequestParams enrich(RequestParams params);


	public List<DataRecord> getApiRequest(RequestParams params) {
		String url = UrlMaker.makeUrl(originUrl(), enrich(params));

		log.info("Sending HTTP request to: {}", url);
		String response = responder.getResponse(url);

		List<DataRecord> records = map(response);
		log.info("Mapped {} records from {}", records.size(), url);

		return records;
	}
}
