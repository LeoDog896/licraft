package com.leodog896.licraft.chess;

import net.minestom.server.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public class PlayerGameManager {
    private static final WeakHashMap<Player, Set<ChessGame>> games = new WeakHashMap<>();

    private static void init(Player player) {
        games.putIfAbsent(player, Collections.newSetFromMap(new WeakHashMap<>()));
    }

    public static Set<ChessGame> get(Player player) {
        return games.get(player);
    }

    public static Optional<ChessGame> getActive(Player player) {
        return games.get(player).stream().filter(game -> game.getInstance().getPlayers().stream().anyMatch(loopPlayer -> player == loopPlayer)).findFirst();
    }

    public static void add(Player player, ChessGame game) {
        init(player);
        games.get(player).add(game);
    }

    public static void remove(Player player, ChessGame game) {
        init(player);
        games.get(player).remove(game);
    }
}
