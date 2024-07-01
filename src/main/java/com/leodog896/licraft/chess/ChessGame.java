package com.leodog896.licraft.chess;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.leodog896.licraft.FullbrightDimension;
import com.leodog896.licraft.Messages;
import com.leodog896.licraft.chess.render.Action;
import com.leodog896.licraft.chess.render.map.MapRenderHandler;
import com.leodog896.licraft.chess.render.GameInterface;
import com.leodog896.licraft.chess.render.map.chessfont.TextChessFont;
import com.leodog896.licraft.chess.render.markup.Selected;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class ChessGame {
    public static final Pos SPAWN_POSITION = new Pos(7.0 / 2, 5, 7.0 / 2);

    private static final Color SELECT_COLOR = new Color(3, 119, 252);

    private static final Sound SELECT_SOUND = Sound.sound(
            Key.key(SoundEvent.BLOCK_LEVER_CLICK.name()),
            Sound.Source.PLAYER, 1F, 1.5F
    );
    private static final Sound MOVE_SOUND = Sound.sound(
            Key.key(SoundEvent.BLOCK_LEVER_CLICK.name()),
            Sound.Source.PLAYER, 1F, 1.2F
    );
    private static final Sound MOVE_UNSUCCESSFUL = Sound.sound(
            Key.key(SoundEvent.ENTITY_SKELETON_STEP.name()),
            Sound.Source.PLAYER, 1F, 0.5F
    );

    private final Set<Player> players = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> spectators = Collections.newSetFromMap(new WeakHashMap<>());
    private final Board board = new Board();
    private int MAX_SIZE = 2;

    private Instance instance;

    private GameInterface gameInterface;
    private final Entity[] maps = new Entity[8 * 8];

    private Square selectedSquare;

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

        instance.eventNode().addChild(eventNode);

        this.instance = instance;

        setRenderHandler(new MapRenderHandler(this, new TextChessFont()));
    }

    public void setRenderHandler(GameInterface gameInterface) {
        if (this.gameInterface != null) this.gameInterface.unload();
        this.gameInterface = gameInterface;
        this.gameInterface.listenOnInteract((tuple) -> {
            Player player = tuple._1();

            if (!players.contains(player)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(
                        Messages.WARNING + "Don't mess with the board :<"
                ));
                return;
            }

            Square square = tuple._2();
            Action action = tuple._3();

            if (action == Action.PRIMARY) {
                if (selectedSquare != null) {
                    this.gameInterface.enableMarkup(new Selected(SELECT_COLOR, selectedSquare));
                    attemptMove(player, selectedSquare, square);
                } else {
                    this.selectedSquare = square;
                    this.gameInterface.enableMarkup(new Selected(SELECT_COLOR, selectedSquare));
                    player.playSound(SELECT_SOUND);
                }
            }
            // TODO: else
        });

        this.gameInterface.load();
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

        gameInterface.rerender(PacketGroupingAudience.of(List.of(player)));

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

    public void attemptMove(Player player, Square from, Square to) {
        selectedSquare = null;
        Move move = new Move(from, to);
        if (board.isMoveLegal(move, true)) {
            board.doMove(move);
            this.gameInterface.move(move);
            this.audience().playSound(MOVE_SOUND, player.getPosition());
        } else {
            player.playSound(MOVE_UNSUCCESSFUL, player.getPosition());
        }
    }

    public void finished() {
        this.gameInterface.unload();

        // TODO: send players & spectators to lobby.
    }
}
