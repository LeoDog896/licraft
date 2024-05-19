package com.leodog896.licraft;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.extras.lan.OpenToLANConfig;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.world.DimensionType;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        DimensionType fullbright = DimensionType.builder(NamespaceID.from("world:full_bright")).ambientLight(2.0f).build();
        MinecraftServer.getDimensionTypeManager().addDimension(fullbright);
        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(fullbright);
        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        OpenToLAN.open(new OpenToLANConfig().eventCallDelay(Duration.of(5, TimeUnit.SECOND)));

        System.out.println("Server started on port 25565");

        server.start("0.0.0.0", 25565);
    }
}
