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
	-Разобраться с New
	-Разобраться с USGS
	-Добавить вспомогательные команды (help,...)
	-Сделать тесты
	-Добавить логгер
 */
