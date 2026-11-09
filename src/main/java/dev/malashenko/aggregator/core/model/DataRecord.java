package dev.malashenko.aggregator.core.model;

import java.time.Instant;
import java.util.Map;

public record DataRecord(
	String source,
	EntityType entityType,
	String entityId,
	Instant observedAt,
	Map<String, Object> metrics
) {}
