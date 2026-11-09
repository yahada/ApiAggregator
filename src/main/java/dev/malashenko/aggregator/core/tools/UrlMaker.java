package dev.malashenko.aggregator.core.tools;

import dev.malashenko.aggregator.core.model.RequestParams;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
@Slf4j
public class UrlMaker {
	public static String makeUrl(String originUrl, RequestParams params) {
		log.info("ORIGIN URL = " + originUrl);

		StringBuilder resultURL = new StringBuilder(originUrl);
		resultURL.append("?");
		String stringParams = params.queryParams().entrySet().stream()
			.map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
			.collect(Collectors.joining("&"));

		log.info("stringParams = " + stringParams);
		resultURL.append(stringParams);
		log.info("RESULT URL = " + resultURL);

		return resultURL.toString();
	}

}
