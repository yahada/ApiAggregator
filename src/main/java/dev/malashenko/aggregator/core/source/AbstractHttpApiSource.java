package dev.malashenko.aggregator.core.source;

import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.model.DataRecord;
import dev.malashenko.aggregator.core.model.RequestParams;
import dev.malashenko.aggregator.core.tools.UrlMaker;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
public abstract class AbstractHttpApiSource implements ApiSource {
	private final HttpResponder responder;
	private final ApiMapper mapper;

	protected AbstractHttpApiSource(HttpResponder responder, ApiMapper mapper) {
		this.responder = responder;
		this.mapper = mapper;
	}

	protected abstract String originUrl();
	protected abstract RequestParams enrich(RequestParams params);
	public List<DataRecord> getApiRequest(RequestParams params) {
		String url = UrlMaker.makeUrl(originUrl(), enrich(params));

		HttpResponse<String> response = responder.getHttpResponse(url);

		return mapper.map(response.body());
	}
}
