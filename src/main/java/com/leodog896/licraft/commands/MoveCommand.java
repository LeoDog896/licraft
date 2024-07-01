package com.leodog896.licraft.commands;

import com.leodog896.licraft.chess.ChessGame;
import com.leodog896.licraft.chess.PlayerGameManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;

import java.util.Optional;

public class MoveCommand extends Command {

    public MoveCommand() {
        super("move");

        setCondition(Conditions::playerOnly);

        var moveArgument = ArgumentType.Literal("move");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            Optional<ChessGame> game = PlayerGameManager.getActive(player);
        }, moveArgument);
    }

}
