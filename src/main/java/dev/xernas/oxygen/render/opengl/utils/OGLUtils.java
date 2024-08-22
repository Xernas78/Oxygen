package dev.xernas.oxygen.render.opengl.utils;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.img.Image;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class OGLUtils {

    public static int requireNotEquals(int obj, int notEqual, String message) throws OpenGLException {
        if (obj == notEqual) throw new OpenGLException(message);
        return obj;
    }

    public static int loadTexture(Path textPath, List<Integer> textures) throws OxygenException {
        Image image = loadImage(textPath);
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

    public static Image loadImage(Path iconPath) throws OxygenException {
        int width, height;
        ByteBuffer imageBuffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Use the MemoryStack for temporary storage
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Convert the Path to an InputStream
            try (InputStream inputStream = Files.newInputStream(iconPath)) {
                // Read the InputStream into a ByteBuffer
                byte[] bytes = inputStream.readAllBytes();
                imageBuffer = ByteBuffer.allocateDirect(bytes.length).put(bytes);
                imageBuffer.flip(); // Reset buffer position to zero for reading

                // Use STBImage to load the image from the ByteBuffer
                ByteBuffer buffer = STBImage.stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelsBuffer, 4);
                if (buffer == null) {
                    throw new OxygenException("Error loading image file: " + STBImage.stbi_failure_reason());
                }

                width = widthBuffer.get();
                height = heightBuffer.get();

                return new Image(width, height, buffer);
            }
        } catch (IOException e) {
            throw new OxygenException("Error loading image file: " + iconPath);
        }
    }

}
