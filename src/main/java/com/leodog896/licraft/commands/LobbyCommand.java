package com.leodog896.licraft.commands;

import com.leodog896.licraft.Lobby;
import com.leodog896.licraft.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby");

        setCondition(Conditions::playerOnly);

        setDefaultExecutor(((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            Messages.PREFIX + "Sending to lobby..."
                    )
            );

            Lobby.sendToLobby(player);
        }));
    }

}
