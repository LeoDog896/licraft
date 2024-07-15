package com.leodog896.licraft.chess.render.map.chessfont;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class GlyphChessFont implements ChessFont {
    private static final int GLYPH_WIDTH = 16;
    private static final int GLYPH_HEIGHT = 16;

    private final BufferedImage spriteSheet;
    private final Map<Piece, BufferedImage> mappedSheet = new HashMap<>();

    public GlyphChessFont() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream spriteSheetStream = classloader.getResourceAsStream("glyph/pieces.png");

        assert spriteSheetStream != null;
        this.spriteSheet = ImageIO.read(spriteSheetStream);

        for (Piece piece : Piece.values()) {
            if (piece != Piece.NONE) {
                this.mappedSheet.put(piece, pieceImage(piece));
            }
        }
    }
    private BufferedImage pieceImage(@NotNull Piece piece) {
        int x = switch (piece.getPieceType()) {
            case PAWN -> 80;
            case KNIGHT -> 48;
            case BISHOP -> 32;
            case ROOK -> 64;
            case QUEEN -> 16;
            case KING -> 0;
            case NONE -> throw new IllegalStateException("Piece is null?");
        };

        int y = piece.getPieceSide() == Side.WHITE ? GLYPH_HEIGHT : 0;

        return spriteSheet.getSubimage(x, y, GLYPH_WIDTH, GLYPH_HEIGHT);
    }

    @Override
    public void render(
            @NotNull Graphics2D renderer,
            @NotNull Piece piece,
            int offsetX,
            int offsetY
    ) {
        renderer.drawImage(
                mappedSheet.get(piece),
                new AffineTransformOp(
                        AffineTransform.getScaleInstance(1.0, 1.0),
                        AffineTransformOp.TYPE_NEAREST_NEIGHBOR
                ),
                offsetX,
                offsetY
        );
    }
}
