package com.leodog896.licraft.chess.render.map;

import net.minestom.server.map.Framebuffer;
import net.minestom.server.map.MapColors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

// TODO: file a minestom issue on this
public class Graphics2DAbsoluteAlphaFramebuffer implements Framebuffer {

    private final byte[] colors = new byte[WIDTH * HEIGHT];
    private final BufferedImage backingImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final Graphics2D renderer;
    private final int[] pixels;

    public Graphics2DAbsoluteAlphaFramebuffer() {
        renderer = backingImage.createGraphics();
        pixels = ((DataBufferInt) backingImage.getRaster().getDataBuffer()).getData();
    }

    public Graphics2D getRenderer() {
        return renderer;
    }

    public BufferedImage getBackingImage() {
        return backingImage;
    }

    public int get(int x, int z) {
        return pixels[x + z * WIDTH]; // stride is always the width of the image
    }

    public Graphics2DAbsoluteAlphaFramebuffer set(int x, int z, int rgb) {
        pixels[x + z * WIDTH] = rgb;
        return this;
    }

    @Override
    public byte[] toMapColors() {
        // TODO: update subparts only
        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                int color = get(x, z);
                if (color == 0) colors[Framebuffer.index(x, z)] = MapColors.NONE.baseColor();
                else colors[Framebuffer.index(x, z)] = MapColors.closestColor(color).getIndex();
            }
        }
        return colors;
    }
}

