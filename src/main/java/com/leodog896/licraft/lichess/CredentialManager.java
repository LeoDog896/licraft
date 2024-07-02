package com.leodog896.licraft.lichess;

import chariot.Client;
import net.minestom.server.entity.Player;

import java.util.WeakHashMap;

public class CredentialManager {
    private static final WeakHashMap<Player, Client> clients = new WeakHashMap<>();

    public static void beginRegistration(Player player) {
//        clients.put(player, Client.auth());
    }
}
