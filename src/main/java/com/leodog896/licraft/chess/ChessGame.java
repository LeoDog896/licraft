package com.leodog896.licraft.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.leodog896.licraft.FullbrightDimension;
import com.leodog896.licraft.Messages;
import com.leodog896.licraft.chess.render.MapRenderHandler;
import com.leodog896.licraft.chess.render.RenderHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class ChessGame {
    public static final Pos SPAWN_POSITION = new Pos(7.0 / 2, 5, 7.0 / 2);

    private final Set<Player> players = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> spectators = Collections.newSetFromMap(new WeakHashMap<>());
    private final Board board = new Board();
    private int MAX_SIZE = 2;

    private Instance instance;

    private RenderHandler renderHandler = new MapRenderHandler(this);
    private final Entity[] maps = new Entity[8 * 8];

    public ChessGame() {
        this(false);
    }

    public PacketGroupingAudience audience() {
        return new PacketGroupingAudience() {
            @Override
            public @NotNull Collection<@NotNull Player> getPlayers() {
                return Stream.concat(players.stream(), spectators.stream()).toList();
            }
        };
    }

    public Board getBoard() {
        return this.board;
    }

    public ChessGame(boolean playground) {
        if (playground) {
            MAX_SIZE = 1;
        }

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

        EventNode<InstanceEvent> eventNode = EventNode.type("join-listener", EventFilter.INSTANCE, (self, event) -> self.getInstance() == instance);

        eventNode.addListener(PlayerMoveEvent.class, event -> {
            if (event.getPlayer().getPosition().y() < 0) {
                event.getPlayer().teleport(SPAWN_POSITION.withDirection(event.getPlayer().getPosition().direction()));
            }
        });

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        this.instance = instance;

        renderHandler.load();
    }

    public void setRenderHandler(RenderHandler renderHandler) {
        this.renderHandler.unload();
        this.renderHandler = renderHandler;
        this.renderHandler.load();
    }

    public Instance getInstance() {
        return this.instance;
    }

    /**
     * Player formally forfeits / permanently leaves a game.
     * @param player The player forfeiting or leaving
     */
    public void forfeit(Player player) {
        boolean wasPlayer = players.remove(player);
        if (!wasPlayer) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    Messages.PREFIX + "You aren't an active player of the game!"
            ));

            return;
        }

        PlayerGameManager.remove(player, this);

        // TODO: confirmation
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have forfeited the game!"
        ));

        // TODO: announce to other players
    }

    public void playerLeave(Player player) {
        boolean wasSpectator = spectators.remove(player);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have left the game world!"
        ));
    }

    public void registerPlayer(Player player) {
        player.setInstance(instance, SPAWN_POSITION.withDirection(player.getPosition().direction()));

        renderHandler.rerender(PacketGroupingAudience.of(List.of(player)));

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
        player.setAllowFlying(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                Messages.PREFIX + "You have joined the game."
        ));

        PlayerGameManager.add(player, this);

        players.add(player);
    }

    public void finished() {
        this.renderHandler.unload();

        // TODO: send players & spectators to lobby.
    }
}
