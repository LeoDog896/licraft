package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.leodog896.licraft.util.StringUtils;
import net.minestom.server.map.Framebuffer;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class TextChessFont implements ChessFont {
    private Font font;
    private FontMetrics metrics;
    private int textHeight;

    public TextChessFont() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream monocraft = classloader.getResourceAsStream("font/Monocraft-no-ligatures.ttf");

        if (monocraft == null) {
            Logger.warn("Monocraft file is null.");
            this.font = Font.getFont("Arial");
            return;
        }

        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, monocraft);
            this.font = this.font.deriveFont(Font.BOLD, 20);
        } catch (FontFormatException | IOException e) {
            Logger.warn(e);
            this.font = Font.getFont("Arial");
        }
    }

    @Override
    public void prepare(@NotNull Graphics2D renderer, int width, int height) {
        renderer.setFont(font);
        // from https://stackoverflow.com/a/27740330/7589775
        this.metrics = renderer.getFontMetrics(font);
        this.textHeight = ((Framebuffer.HEIGHT - (
                metrics.getHeight()
        )) / 2) + metrics.getAscent() - metrics.getDescent();
    }

    @Override
    public void render(
            @NotNull Graphics2D renderer,
            @NotNull Piece piece,
            int offsetX,
            int offsetY
    ) {
        String text = StringUtils.titleCaseWord(Arrays.stream(piece.name().split("_")).toList().getLast());

        // from https://stackoverflow.com/a/27740330/7589775
        int x = (Framebuffer.WIDTH - metrics.stringWidth(text)) / 2;

        // Barely black to go past the alpha check
        renderer.setColor(piece.getPieceSide() == Side.BLACK ? new Color(0, 1, 0) : Color.WHITE);

        renderer.translate(offsetX, offsetY);
        renderer.rotate(Math.PI, Framebuffer.WIDTH / 2.0, Framebuffer.HEIGHT / 2.0);
        renderer.drawString(text, x, textHeight);
        renderer.rotate(Math.PI, Framebuffer.WIDTH / 2.0, Framebuffer.HEIGHT / 2.0);
        renderer.translate(-offsetX, -offsetY);
    }
}
