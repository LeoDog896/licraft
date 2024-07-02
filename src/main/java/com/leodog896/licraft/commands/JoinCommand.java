package com.leodog896.licraft.commands;

import com.leodog896.licraft.Messages;
import com.leodog896.licraft.chess.ChessGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class JoinCommand extends Command {
    public JoinCommand() {
        super("join");

        var id = ArgumentType.Word("id");

        setCondition(Conditions::playerOnly);

        id.setSuggestionCallback((_, _, suggestion) -> {
            String input = suggestion.getInput();

            // TODO: dynamic suggestions
            suggestion.addEntry(new SuggestionEntry("abfnan"));
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            ChessGame game = ChessGame.getById(context.get(id));

            if (game.isPublic() || game.isInvited(player)) {
                game.registerPlayer(player);
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(
                        Messages.WARNING + "You aren't allowed to join this private game!"
                ));
            }
        }, id);
    }
}
