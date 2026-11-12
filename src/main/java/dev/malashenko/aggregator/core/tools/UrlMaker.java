package dev.malashenko.aggregator.core.tools;

import dev.malashenko.aggregator.core.model.RequestParams;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class UrlMaker {
	public static String makeUrl(String originUrl, RequestParams params) {
		if (params.queryParams().isEmpty()) {
			return originUrl;
		}

		String separator = originUrl.contains("?") ? "&" : "?";
		String stringParams = params.queryParams().entrySet().stream()
			.map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
			.collect(Collectors.joining("&"));

		return originUrl + separator + stringParams;
	}
}
