package com.leodog896.licraft.chess.render.markup;

import com.github.bhlangonijr.chesslib.Square;

import java.awt.*;

/**
 * A movement 'indicator', showing that a pawn can move to another pawn
 * @param color
 * @param square
 */
public record MovementIndicator(Color color, Square square) implements Markup {
}
