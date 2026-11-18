package dev.malashenko.aggregator.cli.application;

import dev.malashenko.aggregator.cli.args.DataCommandArgs;
import dev.malashenko.aggregator.cli.args.ShowCommandArgs;
import dev.malashenko.aggregator.core.aggregator.Aggregator;
import dev.malashenko.aggregator.core.http.HttpResponder;
import dev.malashenko.aggregator.core.source.ApiSource;
import dev.malashenko.aggregator.core.source.ApiSourceRegistry;
import dev.malashenko.aggregator.core.storage.DataStorage;
import dev.malashenko.aggregator.core.storage.DataStorageFactory;
import dev.malashenko.aggregator.core.tools.ObjectMapperBuilder;

import java.nio.file.Path;
import java.util.Map;

public class AppFactory {

	private final OutputFilePreparer outputFilePreparer = new OutputFilePreparer();

	public Aggregator createForData(DataCommandArgs args) {
		Path outputPath = OutputPathResolver.resolve(args.filename(), args.format());
		outputFilePreparer.prepare(outputPath, args.writeMode());

		HttpResponder httpResponder = HttpResponder.getDefaultResponder();
		Map<String, ApiSource> sources = ApiSourceRegistry.select(
			httpResponder, args.sourceNames(), ObjectMapperBuilder.buildDefault());
		DataStorage dataStorage = DataStorageFactory.getDataStorage(args.format(), outputPath);

		return new Aggregator(dataStorage, sources);
	}

	public Aggregator createForShow(ShowCommandArgs args) {
		Path outputPath = OutputPathResolver.resolve(args.filename(), args.format());
		DataStorage dataStorage = DataStorageFactory.getDataStorage(args.format(), outputPath);

		return new Aggregator(dataStorage, null);
	}
}
