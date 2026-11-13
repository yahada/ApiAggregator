package dev.malashenko.aggregator.core.tools;

import dev.malashenko.aggregator.core.storage.StorageFormat;

import java.nio.file.Path;

public class FileExtension {

	public static String extension(String filename) {

		int dot = filename.lastIndexOf('.');

		if (dot <= 0 || dot == filename.length() - 1) {
			return "";
		}

		return filename.substring(dot + 1).toLowerCase();
	}

	public static String formatOf(StorageFormat storageFormat) {
		return switch (storageFormat) {
			case StorageFormat.JSON -> "json";
			case StorageFormat.CSV -> "csv" ;
		};
	}
}
