package com.leodog896.licraft;

import com.leodog896.licraft.commands.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.extras.lan.OpenToLANConfig;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.world.DimensionType;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        MojangAuth.init();

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(Lobby.INSTANCE);
            player.setRespawnPoint(Lobby.SPAWN_POSITION);
        }).addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) {
                return;
            }

            final Player player = event.getPlayer();
            player.sendMessage(MiniMessage.miniMessage().deserialize("""
                    <green>Welcome to <b><gradient:green:#34eb89>LICRAFT</gradient></b>!</green>
                    
                    To get started, go to <gold>/register</gold>
                    to connect to your <color:#c7945d>Lichess </color>account
                    or <gold>/play</gold> to get started in a game!
                    
                    For more information, use <gold>/help</gold>.""".trim()));
        });

        OpenToLAN.open(new OpenToLANConfig().eventCallDelay(Duration.of(5, TimeUnit.SECOND)));

        MinecraftServer.getCommandManager().register(new AnalysisCommand());
        MinecraftServer.getCommandManager().register(new LobbyCommand());
        MinecraftServer.getCommandManager().register(new GamesCommand());
        MinecraftServer.getCommandManager().register(new MoveCommand());
        MinecraftServer.getCommandManager().register(new JoinCommand());
        MinecraftServer.getCommandManager().register(new InviteCommand());

        System.out.println("Server started on port 25565.");

        server.start("0.0.0.0", 25565);
    }
}
