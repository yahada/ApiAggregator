package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.core.model.DataRecord;

import java.util.List;

public class RecordPrinter {

	public void printAll(List<DataRecord> records) {
		records.forEach(record -> System.out.println(record.source() + "|" + record.entityId() + "|" + record.metrics()));
	}
}
