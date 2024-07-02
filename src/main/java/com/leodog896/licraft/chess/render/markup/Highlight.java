package com.leodog896.licraft.chess.render.markup;

import com.github.bhlangonijr.chesslib.Square;

import java.awt.*;

public record Highlight(Color color, Square square) implements Markup {
}
