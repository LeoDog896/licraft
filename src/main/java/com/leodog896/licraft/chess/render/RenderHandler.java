package com.leodog896.licraft.chess.render;

import com.github.bhlangonijr.chesslib.move.Move;
import net.minestom.server.adventure.audience.PacketGroupingAudience;

public interface RenderHandler {
    void load();
    void unload();

    /**
     * This does not un/reload entities.
     * Rather, it triggers any extra packet updates
     * whenever a player joins.
     */
    void rerender(PacketGroupingAudience audience);

    void move(Move move);
}
