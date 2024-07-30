package com.leodog896.licraft.commands;

import net.minestom.server.command.builder.Command;

import java.util.List;

public class HelpCommand extends Command {
    public static String[] subHelpCommands = {
            ""
    };

    public HelpCommand() {
        super("help");

        setDefaultExecutor((sender, context) -> {

        });
    }
}
