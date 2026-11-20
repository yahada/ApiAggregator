package dev.malashenko.aggregator.source;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UrlBuilder {

	public static String build(String baseUrl, Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return baseUrl;
		}

		StringBuilder url = new StringBuilder(baseUrl);
		String separator = baseUrl.contains("?") ? "&" : "?";

		for (Map.Entry<String, String> entry : params.entrySet()) {
			url.append(separator)
					.append(encode(entry.getKey()))
					.append("=")
					.append(encode(entry.getValue()));
			separator = "&";
		}
		return url.toString();
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
