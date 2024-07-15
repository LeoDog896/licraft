package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface ChessFont {
    /**
     * Prepares the chess font for rendering on the renderer
     * to avoid unnecessary repetitive calls.
     *
     * @param renderer The renderer to apply preparations to.
     * @param width The width of the board.
     * @param height The height of the board
     */
    default void prepare(
            @NotNull Graphics2D renderer,
            int width,
            int height
    ) {

    }

    /**
     * Render a piece on a board with a certain offset.
     * Precondition: Piece#getPieceType is never NONE.
     *
     * @param renderer The renderer to apply the piece on.
     * @param piece The piece to render.
     * @param offsetX x offset
     * @param offsetY y offset
     */
    void render(
            @NotNull Graphics2D renderer,
            @NotNull Piece piece,
            int offsetX,
            int offsetY
    );
}
