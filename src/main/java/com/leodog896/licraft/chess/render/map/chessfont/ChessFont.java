package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;

import java.awt.*;

public interface ChessFont {
    void render(Graphics2D renderer, Piece piece);
}
