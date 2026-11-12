package dev.malashenko.aggregator.cli.application;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class CommandLoop {

	private static final String READY_MESSAGE = "ready. commands: data API=<name> [param=value ...] | show [API=<name>]";

	private final CommandExecutor commandExecutor;

	public CommandLoop(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	public void start() {
		try (Scanner scanner = new Scanner(System.in)) {
			log.info(READY_MESSAGE);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.isBlank()) {
					continue;
				}

				if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
					break;
				}

				commandExecutor.execute(line);
			}
		}
	}
}
