package com.leodog896.licraft.chess.render.markup;

import com.github.bhlangonijr.chesslib.Square;

import java.awt.*;

public record RecentMovement(Color color, Square from, Square to) implements Markup {
}
