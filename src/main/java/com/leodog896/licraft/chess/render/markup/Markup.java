package com.leodog896.licraft.chess.render.markup;

import java.awt.*;

public sealed interface Markup permits Arrow, Selected, MovementIndicator, Circle, Threaten, RecentMovement {
    Color color();
}
