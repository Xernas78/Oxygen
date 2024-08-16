package dev.xernas.oxygen.render.opengl.utils;

import dev.xernas.oxygen.engine.resource.img.Image;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class OGLUtils {

    public static int requireNotEquals(int obj, int notEqual, String message) throws OpenGLException {
        if (obj == notEqual) throw new OpenGLException(message);
        return obj;
    }

    public static int loadTexture(String absoluteTextPath, List<Integer> textures) throws OxygenException {
        Image image = loadImage(absoluteTextPath);
        ByteBuffer buffer = image.getData();
        int width = image.getWidth();
        int height = image.getHeight();

        int id = OGLUtils.requireNotEquals(glGenTextures(), 0, "Error creating texture");
        textures.add(id);
        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    public static Image loadImage(String absoluteTextPath) throws OxygenException {
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            buffer = STBImage.stbi_load(absoluteTextPath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new OxygenException("Error loading image file: " + STBImage.stbi_failure_reason());

            width = widthBuffer.get();
            height = heightBuffer.get();
        }
        return new Image(width, height, buffer);
    }

}
