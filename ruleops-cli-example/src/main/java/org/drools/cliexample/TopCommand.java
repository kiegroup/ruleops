package org.drools.cliexample;

import picocli.CommandLine;

@io.quarkus.picocli.runtime.annotations.TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {FindNamespaceCommand.class, FindPodCommand.class})
public class TopCommand {
}

