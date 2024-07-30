package com.leodog896.licraft.chess.render.map;

import net.minestom.server.map.Framebuffer;
import net.minestom.server.map.LargeFramebuffer;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.LargeFramebufferDefaultView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

// TODO: file an issue on this
public class LargeGraphics2DAlphaFramebuffer implements LargeFramebuffer {

    private final BufferedImage backingImage;
    private final Graphics2D renderer;
    private final int[] pixels;
    private final int width;
    private final int height;

    public LargeGraphics2DAlphaFramebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        backingImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
        return pixels[x + z * width]; // stride is always the width of the image
    }

    public LargeGraphics2DAlphaFramebuffer set(int x, int z, int rgb) {
        pixels[x + z * width] = rgb;
        return this;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Framebuffer createSubView(int left, int top) {
        return new LargeFramebufferDefaultView(this, left, top);
    }

    @Override
    public byte getMapColor(int x, int y) {
        int rawColor = get(x, y);
        if (rawColor == 0) return MapColors.NONE.baseColor();
        int color = rawColor & 0x00FFFFFF;
        return MapColors.closestColor(color).getIndex();
    }
}
