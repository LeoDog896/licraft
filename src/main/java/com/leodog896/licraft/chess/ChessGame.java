package com.leodog896.licraft.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.leodog896.licraft.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ChessGame {
    private final Set<Player> players = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> spectators = Collections.newSetFromMap(new WeakHashMap<>());
    private final Board board;
    private static int MAX_SIZE = 2;

    public ChessGame() {
        this(false);
    }

    public ChessGame(boolean analysis) {
        if (analysis) {
            MAX_SIZE = 1;
        }

        board = new Board();
    }

    public void forfeit(Player player) {
        boolean wasPlayer = players.remove(player);
        if (!wasPlayer) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    Messages.PREFIX + "You aren't an active player of the game!"
            ));
            return;
        }

        // TODO: confirmation
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have forfeited the game!"
        ));

        // TODO: announce to other players
    }

    public void unregisterPlayer(Player player) {
        boolean wasSpectator = spectators.remove(player);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have left the game world!"
        ));
    }

    public void registerPlayer(Player player) {
        player.setAllowFlying(true);

        if (players.size() > MAX_SIZE) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format(
                            "%sThere %s already <bold>%s</bold> player%s in this lobby - you are now a spectator.",
                            Messages.PREFIX,
                            players.size() == 1 ? "is" : "are",
                            players.size(),
                            players.size() > 1 ? "s" : ""
                    )
            ));

            spectators.add(player);

            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have joined the game."
        ));
        players.add(player);
    }
}
