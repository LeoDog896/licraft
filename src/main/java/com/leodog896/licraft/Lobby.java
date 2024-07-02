package com.leodog896.licraft;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

public class Lobby {
    public static final Pos SPAWN_POSITION = new Pos(0, 3, 0);
    public static final Instance INSTANCE = makeLobby();

    private static Instance makeLobby() {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        // Create the instance
        InstanceContainer lobby = instanceManager.createInstanceContainer(FullbrightDimension.key);

        // Set the ChunkGenerator
        lobby.setGenerator(unit ->
                unit.modifier().fillHeight(0, 1, Block.BARRIER));

        lobby.eventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                player.setGameMode(GameMode.ADVENTURE);
            }
        });

        return lobby;
    }

    public static void sendToLobby(Player player) {
        player.setInstance(INSTANCE, SPAWN_POSITION);
    }
}
