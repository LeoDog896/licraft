package com.leodog896.licraft.chess;

import net.minestom.server.instance.Instance;

import java.util.WeakHashMap;

public class ChessManager {
    public static WeakHashMap<Instance, ChessGame> games = new WeakHashMap<>();
}
