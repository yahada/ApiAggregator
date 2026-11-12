package dev.malashenko.aggregator;
import dev.malashenko.aggregator.cli.application.CliApplication;


public class Main {
	public static void main(String[] args) {
		CliApplication application = new CliApplication();
		System.exit(application.run(args));
	}
}


/*
TODO:
	-RuntimeExp свой
	-Сделать тесты
	-Добавить логгер
 */
