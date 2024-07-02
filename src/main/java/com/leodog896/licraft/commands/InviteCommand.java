package com.leodog896.licraft.commands;

import com.leodog896.licraft.Messages;
import com.leodog896.licraft.chess.ChessGame;
import com.leodog896.licraft.chess.PlayerGameManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.Optional;

public class InviteCommand extends Command {
    public InviteCommand() {
        super("invite");

        setCondition(Conditions::playerOnly);

        setDefaultExecutor((sender, context) -> {
            // TODO: show inventory UI of player heads to invite
        });

        var playerArgument = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true);

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            Optional<ChessGame> activeGame = PlayerGameManager.getActive(player);

            if (activeGame.isEmpty()) {
                player.sendMessage(Messages.NOT_IN_GAME);
                return;
            }

            ChessGame game = activeGame.get();

            EntityFinder selector = context.get(playerArgument);
            Player foundPlayer = selector.findFirstPlayer(player);

            if (foundPlayer == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(
                        String.format(
                                "%sPlayer <red>%s</red> not found!",
                                Messages.ERROR,
                                selector
                        )
                ));
                return;
            }

            game.invite(foundPlayer);
            foundPlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("%s%s invited you to a game!",
                            Messages.PREFIX,
                            player.getDisplayName()
                    )));
        }, playerArgument);
    }
}
