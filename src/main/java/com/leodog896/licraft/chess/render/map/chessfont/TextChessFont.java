package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import net.minestom.server.map.Framebuffer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TextChessFont implements ChessFont {
    private Font font;

    public TextChessFont() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream monocraft = classloader.getResourceAsStream("Monocraft-no-ligatures.ttf");

        // TODO: use logger
        if (monocraft == null) {
            System.err.println("WARNING: Monocraft file is null.");
            this.font = Font.getFont("Arial");
            return;
        }

        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, monocraft);
            this.font = this.font.deriveFont(Font.BOLD, 20);
        } catch (FontFormatException | IOException e) {
            System.err.println(e);
            this.font = Font.getFont("Arial");
        }
    }

    @Override
    public void render(Graphics2D renderer, Piece piece) {
        String text = Arrays.stream(piece.name().split("_"))
                .map(word ->
                        String.valueOf(word.charAt(0)).toUpperCase()
                                + word.toLowerCase().substring(1)
                )
                .skip(1)
                .toList().getFirst();

        // from https://stackoverflow.com/a/27740330/7589775
        FontMetrics metrics = renderer.getFontMetrics(font);
        int x = (Framebuffer.WIDTH - metrics.stringWidth(text)) / 2;
        int y = ((Framebuffer.HEIGHT - (
                metrics.getHeight()
        )) / 2) + metrics.getAscent() - metrics.getDescent();
        System.out.println(y);

        renderer.setColor(new Color(0, 0, 0, 0));
        renderer.drawRect(0, 0, Framebuffer.WIDTH, Framebuffer.HEIGHT);
        // Barely black to go past the alpha check
        renderer.setColor(piece.getPieceSide() == Side.BLACK ? new Color(0, 1, 0) : Color.WHITE);
        renderer.setFont(font);
        renderer.drawString(text, x, y);
    }
}
