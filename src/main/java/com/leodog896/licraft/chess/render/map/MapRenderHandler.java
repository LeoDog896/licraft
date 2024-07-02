package com.leodog896.licraft.chess.render.map;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.leodog896.licraft.chess.ChessGame;
import com.leodog896.licraft.chess.render.Action;
import com.leodog896.licraft.chess.render.GameInterface;
import com.leodog896.licraft.chess.render.map.chessfont.TextChessFont;
import com.leodog896.licraft.chess.render.markup.Circle;
import com.leodog896.licraft.chess.render.markup.Markup;
import com.leodog896.licraft.chess.render.markup.MovementIndicator;
import com.leodog896.licraft.chess.render.markup.Selected;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.Framebuffer;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MapRenderHandler implements GameInterface {
    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;
    private static final int BOARD_SIZE = BOARD_WIDTH * BOARD_HEIGHT;
    private static final int WIDTH = Framebuffer.WIDTH * BOARD_WIDTH;
    private static final int HEIGHT = Framebuffer.HEIGHT * BOARD_HEIGHT;

    private static int MAP_ID_COUNTER = 1;

    private static final DirectFramebuffer CLEAR_BUFFER = clearBuffer();

    private int first_id;
    private Entity[] screen = new Entity[BOARD_WIDTH * BOARD_HEIGHT];
    private WeakHashMap<Entity, Square> entityToSquare = new WeakHashMap<>();

    private Set<Markup> markup = Collections.newSetFromMap(new WeakHashMap<>());

    private ChessGame game;
    private TextChessFont textChessFont;

    private EventNode<InstanceEvent> eventNode = EventNode.type("render-event", EventFilter.INSTANCE);

    public MapRenderHandler(ChessGame game, TextChessFont textChessFont) {
        this.game = game;
        this.textChessFont = textChessFont;
    }

    private static DirectFramebuffer clearBuffer() {
        DirectFramebuffer framebuffer = new DirectFramebuffer();
        for (int i = 0; i < Framebuffer.WIDTH; i++) {
            for (int j = 0; j < Framebuffer.HEIGHT; j++) {
                framebuffer.set(i, j, MapColors.NONE.baseColor());
            }
        }

        return framebuffer;
    }

    private static Tuple2<Integer, Integer> corner(Square square) {
        return new Tuple2<>(
            Framebuffer.WIDTH * square.getFile().ordinal(),
            Framebuffer.HEIGHT * square.getRank().ordinal()
        );
    }

    private static Tuple2<Integer, Integer> center(Square square) {
        return new Tuple2<>(
                Framebuffer.WIDTH * square.getFile().ordinal() + Framebuffer.WIDTH / 2,
                Framebuffer.HEIGHT * square.getRank().ordinal() + Framebuffer.HEIGHT / 2
        );
    }

    private void renderBoard(Board board, PacketGroupingAudience audience) {
        LargeGraphics2DAlphaFramebuffer framebuffer = new LargeGraphics2DAlphaFramebuffer(
                WIDTH,
                HEIGHT
        );

        framebuffer.getRenderer().setColor(Color.BLACK);
        framebuffer.getRenderer().drawRect(0, 0, WIDTH, HEIGHT);

        this.textChessFont.prepare(framebuffer.getRenderer(), WIDTH, HEIGHT);

        for (Markup mark : markup) {
            framebuffer.getRenderer().setColor(mark.color());
            switch (mark) {
                case Circle circle -> framebuffer.getRenderer().fillOval(
                        center(circle.square())._1(),
                        center(circle.square())._2(),
                        Framebuffer.WIDTH,
                        Framebuffer.HEIGHT
                );
                case Selected selected -> framebuffer.getRenderer().fillRect(
                    corner(selected.square())._1(),
                        corner(selected.square())._2(),
                    Framebuffer.WIDTH,
                    Framebuffer.HEIGHT
                );
                case MovementIndicator indicator -> framebuffer.getRenderer().fillOval(
                    center(indicator.square())._1() - Framebuffer.WIDTH / 6,
                    center(indicator.square())._2() - Framebuffer.WIDTH / 6,
                    Framebuffer.WIDTH / 3,
                    Framebuffer.HEIGHT / 3
                );
                default -> {}
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            Square square = Square.squareAt(i);
            Piece piece = board.getPiece(square);

            if (piece == Piece.NONE) {
                continue;
            }

            this.textChessFont.render(
                    framebuffer.getRenderer(),
                    piece,
                    Framebuffer.WIDTH * square.getFile().ordinal(),
                    Framebuffer.HEIGHT * square.getRank().ordinal()
            );
        }

        for (int i = first_id; i < first_id + BOARD_SIZE; i++) {
            audience.sendGroupedPacket(framebuffer.preparePacket(
                    i,
                    ((i - first_id) % 8) * Framebuffer.WIDTH,
                    ((i - first_id) / 8) * Framebuffer.HEIGHT
            ));
        }
    }

    @Override
    public void rerender(PacketGroupingAudience audience) {
        renderBoard(game.getBoard(), audience);
    }

    @Override
    public void load() {
        first_id = MAP_ID_COUNTER;

        for (int i = 0; i < BOARD_SIZE; i++) {
            Entity entity = new Entity(EntityType.ITEM_FRAME);

            entity.setInvisible(true);

            ItemFrameMeta itemFrameMeta = (ItemFrameMeta) entity.getEntityMeta();

            ItemStack mapItem = ItemStack.builder(Material.FILLED_MAP)
                    .set(ItemComponent.MAP_ID, MAP_ID_COUNTER++)
                    .build();

            itemFrameMeta.setItem(mapItem);
            itemFrameMeta.setOrientation(ItemFrameMeta.Orientation.UP);

            // start from the bottom left to correlate with a1 - h8
            // add 0.5 for the item frame size
            entity.setInstance(game.getInstance(), new Pos(
                    0.5 + i % BOARD_WIDTH,
                    2.1,
                    0.5 + (double) (i / BOARD_HEIGHT)
            ).withDirection(new Pos(0, 1, 0)));

            screen[i] = entity;
            entityToSquare.put(entity, Square.squareAt(i));
        }

        game.getInstance().eventNode().addChild(eventNode);

        renderBoard(game.getBoard(), game.audience());
    }

    @Override
    public void move(Move move) {
        rerender(game.audience());
    }

    @Override
    public void unload() {
        for (Entity entity : screen) {
            entity.remove();
        }

        game.getInstance().eventNode().removeChild(eventNode);
    }

    @Override
    public void listenOnInteract(Consumer<Tuple3<Player, Square, Action>> consumer) {
        eventNode.addListener(EntityAttackEvent.class, event -> {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            Square square = entityToSquare.get(event.getTarget());

            if (square == null) {
                return;
            }

            consumer.accept(new Tuple3<>(
                    player,
                    square,
                    Action.PRIMARY
            ));
        });
    }

    @Override
    public void enableMarkup(Markup markup) {
        this.markup.add(markup);
        rerender(game.audience());
    }

    @Override
    public void disableMarkup(Markup markup) {
        this.markup.add(markup);
        rerender(game.audience());
    }

    @Override
    public void clearMarkup(Predicate<Markup> predicate) {
        this.markup = this.markup.stream().filter(predicate).collect(Collectors.toSet());
        rerender(game.audience());
    }
}
