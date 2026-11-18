package dev.malashenko.aggregator.core.model;

import java.time.Instant;
import java.util.Map;

public record DataRecord(
	String source,
	String entityId,
	Instant timestamp,
	Map<String, Object> metrics
) {}
