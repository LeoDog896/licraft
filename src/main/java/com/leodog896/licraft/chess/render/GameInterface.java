package com.leodog896.licraft.chess.render;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.leodog896.licraft.chess.render.markup.*;
import io.vavr.Tuple3;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface GameInterface {
    void load();
    void unload();

    void listenOnInteract(Consumer<Tuple3<Player, Square, Action>> consumer);

    /**
     * This does not un/reload entities.
     * Rather, it triggers any extra packet updates
     * whenever a player joins.
     */
    void rerender(PacketGroupingAudience audience);

    void move(Move move);

    void enableMarkup(Markup markup, boolean render);
    void disableMarkup(Markup markup, boolean render);

    default void clearMarkup(boolean render) {
        clearMarkup(_ -> true, render);
    };
    void clearMarkup(Predicate<Markup> predicate, boolean render);

    default void clearDecorativeMarkup(boolean render) {
        clearMarkup(markup -> switch (markup) {
            case Arrow _ -> false;
            case Circle _ -> false;
            case MovementIndicator _ -> true;
            case RecentMovement _ -> true;
            case Selected _ -> true;
            case Threaten _ -> true;
        }, render);
    }

    default void clearInformativeMarkup(boolean render) {
        clearMarkup(markup -> switch (markup) {
            case Arrow _ -> true;
            case Circle _ -> true;
            case MovementIndicator _ -> false;
            case RecentMovement _ -> false;
            case Selected _ -> false;
            case Threaten _ -> false;
        }, render);
    }
}
