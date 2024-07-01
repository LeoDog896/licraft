package com.leodog896.licraft.chess;

import com.leodog896.licraft.FullbrightDimension;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

public class ChessInstance {
    public static final Pos SPAWN_POSITION = new Pos(7.0 / 2, 5, 7.0 / 2);

    public static Instance generate() {
        InstanceManager manager = MinecraftServer.getInstanceManager();
        Instance instance = manager.createInstanceContainer(FullbrightDimension.key);

        instance.setGenerator(unit -> {
            for (int i = (int) Math.max(0, unit.absoluteStart().x()); i < (int) Math.min(8, unit.absoluteEnd().x()); i++) {
                for (int j = (int) Math.max(0, unit.absoluteStart().z()); j < (int) Math.min(8, unit.absoluteEnd().z()); j++) {
                    if ((i + (j % 2 == 0 ? 1 : 0)) % 2 == 0) {
                        unit.modifier().setBlock(i, 1, j, Block.GREEN_CONCRETE);
                    } else {
                        unit.modifier().setBlock(i, 1, j, Block.MOSS_BLOCK);
                    }
                }
            }
        });

        ChessGame game = new ChessGame();
        ChessManager.games.put(instance, game);

        EventNode<InstanceEvent> eventNode = EventNode.type("join-listener", EventFilter.INSTANCE, (self, event) -> self.getInstance() == instance);
        eventNode.addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                game.registerPlayer(player);
            }
        });

        eventNode.addListener(RemoveEntityFromInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                game.unregisterPlayer(player);
            }
        });

        eventNode.addListener(PlayerMoveEvent.class, event -> {
            if (event.getPlayer().getPosition().y() < 0) {
                event.getPlayer().teleport(SPAWN_POSITION.withDirection(event.getPlayer().getPosition().direction()));
            }
        });

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        return instance;
    }
}
