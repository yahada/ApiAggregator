package dev.malashenko.aggregator.core.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperBuilder {

	public static ObjectMapper buildDefault() {
		return new ObjectMapper()
			       .registerModule(new JavaTimeModule())
			       .registerModule(new Jdk8Module())
			       .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
