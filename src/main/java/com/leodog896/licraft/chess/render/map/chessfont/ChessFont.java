package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;

import java.awt.*;

public interface ChessFont {
    void prepare(
            Graphics2D renderer,
            int width,
            int height
    );

    void render(
            Graphics2D renderer,
            Piece piece,
            int offsetX,
            int offsetY
    );
}
