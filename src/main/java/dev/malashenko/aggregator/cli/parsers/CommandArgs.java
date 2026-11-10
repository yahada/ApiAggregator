package dev.malashenko.aggregator.cli.parsers;

public sealed interface CommandArgs permits DataCommandArgs, ShowCommandArgs {}
