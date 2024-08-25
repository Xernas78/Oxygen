package dev.xernas.oxygen.engine.resource.formats;

import java.nio.ByteBuffer;

public class Image {

    private final int width;
    private final int height;
    private final ByteBuffer data;

    public Image(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getData() {
        return data;
    }

    public static Image fromByteArray(int width, int height, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();
        return new Image(width, height, buffer);
    }
}
