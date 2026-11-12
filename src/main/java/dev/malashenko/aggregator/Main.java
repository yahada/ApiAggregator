package dev.malashenko.aggregator;
import dev.malashenko.aggregator.cli.application.Application;
import dev.malashenko.aggregator.cli.parsers.*;


public class Main {
	public static void main(String[] args) {
		Application application = new Application();
		int exitCode = application.run(args);

		if (exitCode != 0) {
			System.exit(exitCode);
		}
	}
}


/*
TODO:
	-RuntimeExp свой
	-Сделать тесты
	-Добавить логгер
 */
