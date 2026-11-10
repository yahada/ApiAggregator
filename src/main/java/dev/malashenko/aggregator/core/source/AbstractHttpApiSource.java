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

	public List<DataRecord> getApiRequest(RequestParams params) {
		String url = UrlMaker.makeUrl(originUrl(), params);
		System.out.println("[DEBUG] GET " + url);

		HttpResponse<String> response = responder.getHttpResponse(url);
		System.out.println("[DEBUG] status=" + response.statusCode()
			                   + " body=" + response.body().substring(0, Math.min(200, response.body().length())));
		return mapper.map(response.body());
	}
}
