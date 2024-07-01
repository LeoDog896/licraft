package com.leodog896.licraft.commands;

import com.leodog896.licraft.chess.ChessGame;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class AnalysisCommand extends Command {

    public AnalysisCommand() {
        super("analyze");

        setDefaultExecutor((sender, context) -> {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("LICRAFT: You must be a player to run this command!");
                return;
            }

            ChessGame game = new ChessGame(true);

            game.registerPlayer(player);
        });
    }

}
