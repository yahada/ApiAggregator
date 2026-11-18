package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.util.List;

public class RecordPrinter {

	public static void printAll(List<DataRecord> records) {
		records.forEach(RecordPrinter::print);
	}

	private static void print(DataRecord record) {
		System.out.printf(
			"""
			API: %s
			Record ID: %s
			Timestamp: %s
			Metrics: %s
			%n
			""",
			record.source(),
			record.entityId(),
			record.timestamp(),
			record.metrics()
		);
	}
}
