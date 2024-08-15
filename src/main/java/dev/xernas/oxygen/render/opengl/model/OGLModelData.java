package dev.xernas.oxygen.render.opengl.model;

import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.IOGLObject;
import dev.xernas.oxygen.render.opengl.utils.BufferUtils;
import dev.xernas.oxygen.render.opengl.utils.OGLUtils;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModelData;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class OGLModelData implements IModelData {

    private VAO vao;
    private final List<Integer> textures = new ArrayList<>();

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private final String absoluteTexturePath;

    private int id;
    private int indicesCount;
    private int textureId;

    private FloatBuffer verticesBuffer;
    private IntBuffer indicesBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer textureCoordsBuffer;

    public OGLModelData(float[] vertices, int[] indices, float[] normals, float[] textureCoords, String absoluteTexturePath) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.absoluteTexturePath = absoluteTexturePath;
    }

    public OGLModelData(OGLModelData modelData, float[] textureCoords, String absoluteTexturePath) {
        this.vertices = modelData.vertices;
        this.indices = modelData.indices;
        this.normals = modelData.normals;
        this.textureCoords = textureCoords;
        this.absoluteTexturePath = absoluteTexturePath;
    }

    @Override
    public void init() throws OxygenException {
        vao = new VAO();
        vao.init();
        indicesBuffer = vao.storeIndicesBuffer(indices);
        verticesBuffer = vao.storeDataInAttributeList(0, 3, vertices);
        if (hasTexture()) textureCoordsBuffer = vao.storeDataInAttributeList(1, 2, textureCoords);
        if (hasNormals()) normalsBuffer = vao.storeDataInAttributeList(2, 3, normals);
        unbind();
        this.id = vao.getVaoId();
        this.indicesCount = indices.length;
        if (hasTexture()) textureId = OGLUtils.loadTexture(absoluteTexturePath, textures);
    }

    @Override
    public void cleanup() throws OxygenException {
        vao.cleanup();
        for (int texture : textures) glDeleteTextures(texture);
        MemoryUtil.memFree(verticesBuffer);
        MemoryUtil.memFree(indicesBuffer);
        if (hasTexture()) MemoryUtil.memFree(textureCoordsBuffer);
        if (hasNormals()) MemoryUtil.memFree(normalsBuffer);
    }

    public void bind() throws OpenGLException {
        vao.bind();
        if (hasTexture()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
        }
    }

    public void unbind() throws OpenGLException {
        vao.unbind();
        if (hasTexture()) glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public int getVertexCount() {
        return vertices.length / 3;
    }

    @Override
    public boolean hasTexture() {
        return absoluteTexturePath != null && !absoluteTexturePath.isEmpty() && textureCoords != null && textureCoords.length > 0;
    }

    @Override
    public boolean hasNormals() {
        return normals != null && normals.length > 0;
    }

    public int getId() {
        return id;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public int getTextureId() {
        return textureId;
    }

    public static class VAO implements IOGLObject {

        private int vaoId;

        private final List<VBO> vbos = new ArrayList<>();

        @Override
        public void bind() throws OpenGLException {
            glBindVertexArray(vaoId);
            for (VBO vbo : vbos) vbo.bind();
        }

        @Override
        public void unbind() throws OpenGLException {
            glBindVertexArray(0);
            for (VBO vbo : vbos) vbo.unbind();
        }

        @Override
        public void init() throws OxygenException {
            vaoId = glGenVertexArrays();
            bind();
        }

        @Override
        public void cleanup() throws OxygenException {
            glDeleteVertexArrays(vaoId);
            for (VBO vbo : vbos) vbo.cleanup();
        }

        public IntBuffer storeIndicesBuffer(int[] indices) throws OxygenException {
            int type = GL_ELEMENT_ARRAY_BUFFER;
            VBO vbo = new VBO(type);
            vbo.init();
            vbos.add(vbo);
            IntBuffer buffer = BufferUtils.storeDataInIntBuffer(indices);
            glBufferData(type, buffer, GL_STATIC_DRAW);
            return buffer;
        }

        public FloatBuffer storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) throws OxygenException {
            int type = GL_ARRAY_BUFFER;
            VBO vbo = new VBO(type);
            vbo.init();
            vbos.add(vbo);
            FloatBuffer buffer = BufferUtils.storeDataInFloatBuffer(data);
            glBufferData(type, buffer, GL_STATIC_DRAW);
            glVertexAttribPointer(attributeNumber, vertexCount, GL_FLOAT, false, 0, 0);
            vbo.unbind();
            return buffer;
        }

        public int getVaoId() {
            return vaoId;
        }
    }

    public static class VBO implements IOGLObject {

            private int vboId;

            private final int type;

            public VBO(int type) {
                this.type = type;
            }

            @Override
            public void bind() throws OpenGLException {
                glBindBuffer(type, vboId);
            }

            @Override
            public void unbind() throws OpenGLException {
                glBindBuffer(type, 0);
            }

            @Override
            public void init() throws OxygenException {
                vboId = glGenBuffers();
                bind();
            }

            @Override
            public void cleanup() throws OxygenException {
                unbind();
                glDeleteBuffers(vboId);
            }

        public int getVboId() {
            return vboId;
        }
    }
}