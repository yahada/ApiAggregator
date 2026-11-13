package dev.malashenko.aggregator.core.model;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record DataRecord(
	String source,
	String entityId,
	Optional<Instant> observedAt,
	Map<String, Object> metrics
) {}
