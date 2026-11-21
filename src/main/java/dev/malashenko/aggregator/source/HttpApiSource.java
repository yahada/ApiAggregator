package dev.malashenko.aggregator.source;

import dev.malashenko.aggregator.model.DataRecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public abstract class HttpApiSource {
	public abstract Map<String, String> defaultParams();

	public abstract List<DataRecord> fetch(Map<String, String> parameters) throws IOException, InterruptedException;

	public abstract List<DataRecord> map(String json) throws IOException;

	protected String buildUrl(String baseUrl, Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return baseUrl;
		}

		StringBuilder url = new StringBuilder(baseUrl);
		String separator = baseUrl.contains("?") ? "&" : "?";

		for (Map.Entry<String, String> entry : params.entrySet()) {
			url.append(separator)
				.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
				.append("=")
				.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
			separator = "&";
		}
		return url.toString();
	}
}
