package dev.malashenko.aggregator.cli;

import lombok.extern.slf4j.Slf4j;

import dev.malashenko.aggregator.model.DataRecord;

import java.util.List;
import java.util.Map;

@Slf4j
public class RecordPrinter {

	public static void printAll(List<DataRecord> records) {
		log.info("Printing {} records", records.size());
		records.forEach(RecordPrinter::print);
	}

	private static void print(DataRecord record) {
		System.out.printf(
			"""
				Source: %s
				Entity ID: %s
				Timestamp: %s
				Metrics:
				""",
			record.source(),
			record.entityId(),
			record.timestamp()
		);
		for (Map.Entry<String, Object> entry : record.metrics().entrySet()) {
			System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
		}
		System.out.println();
	}
}
