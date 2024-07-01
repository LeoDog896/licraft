package com.leodog896.licraft.chess.render;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.leodog896.licraft.chess.render.markup.Markup;
import io.vavr.Tuple3;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;

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

    /**
     * Toggles markup on the renderer
     */
    void markup(Markup markup);

    void clearMarkup();
}
