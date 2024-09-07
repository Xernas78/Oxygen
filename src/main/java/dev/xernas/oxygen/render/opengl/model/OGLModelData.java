package dev.xernas.oxygen.render.opengl.model;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.IOGLObject;
import dev.xernas.oxygen.render.opengl.utils.BufferUtils;
import dev.xernas.oxygen.render.opengl.utils.OGLUtils;
import dev.xernas.oxygen.render.model.IModelData;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class OGLModelData implements IModelData {

    private static int uniqueIdCounter = 0;
    private static OGLModelData previousModel = null;
    private static final Map<Integer, Boolean> cleanedUp = new HashMap<>();

    private VAO vao;
    private final List<Integer> textures = new ArrayList<>();

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private final Path texturePath;
    private final int uniqueId = createUniqueId();
    private final boolean is2D;

    private int id;
    private int indicesCount;
    private int textureId;

    private FloatBuffer verticesBuffer = null;
    private IntBuffer indicesBuffer = null;
    private FloatBuffer normalsBuffer = null;
    private FloatBuffer textureCoordsBuffer = null;

    public OGLModelData(float[] vertices, int[] indices, float[] normals, float[] textureCoords, Path texturePath) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.texturePath = texturePath;
        this.is2D = process2D();
    }

    @Override
    public void init() throws OxygenException {
        if (Objects.nonNull(previousModel)) {
            if (Arrays.equals(vertices, previousModel.vertices) && Arrays.equals(indices, previousModel.indices) && Arrays.equals(normals, previousModel.normals) && Arrays.equals(textureCoords, previousModel.textureCoords)) {
                vao = previousModel.vao;
                id = previousModel.id;
                indicesCount = previousModel.indicesCount;
                verticesBuffer = previousModel.verticesBuffer;
                indicesBuffer = previousModel.indicesBuffer;
                normalsBuffer = previousModel.normalsBuffer;
                textureCoordsBuffer = previousModel.textureCoordsBuffer;
            }
            if (Objects.equals(previousModel.texturePath, texturePath)) textureId = previousModel.textureId;
        }
        if (Objects.nonNull(vao)) {
            if (textureId == 0) if (hasTexture()) textureId = OGLUtils.loadTexture(texturePath, textures);
            previousModel = this;
            return;
        }
        vao = new VAO();
        vao.init();
        this.id = vao.getVaoId();
        indicesBuffer = vao.storeIndicesBuffer(indices);
        this.indicesCount = indices.length;
        verticesBuffer = vao.storeDataInAttributeList(0, 3, vertices);
        if (hasTexture()) textureCoordsBuffer = vao.storeDataInAttributeList(1, 2, textureCoords);
        if (hasNormals()) normalsBuffer = vao.storeDataInAttributeList(2, 3, normals);
        if (textureId == 0) if (hasTexture()) textureId = OGLUtils.loadTexture(texturePath, textures);
        unbind();
        previousModel = this;
    }

    @Override
    public void cleanup() throws OxygenException {
        vao.cleanup();
        for (int texture : textures) glDeleteTextures(texture);
        if (!cleanedUp.getOrDefault(id, false)) {
            MemoryUtil.memFree(verticesBuffer);
            MemoryUtil.memFree(indicesBuffer);
            if (hasTexture()) MemoryUtil.memFree(textureCoordsBuffer);
            if (hasNormals()) MemoryUtil.memFree(normalsBuffer);
            cleanedUp.put(id, true);
        }
    }

    public static void clearClean() {
        cleanedUp.clear();
    }

    public void bind() throws OpenGLException {
        vao.bind();
        if (hasTexture()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
        }
        else {
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        ModelRenderer.bindsPerFrame++;
    }

    public void unbind() throws OpenGLException {
        vao.unbind();
        if (hasTexture()) glBindTexture(GL_TEXTURE_2D, 0);
        ModelRenderer.unbindsPerFrame++;
    }

    @Override
    public int getVertexCount() {
        return vertices.length / 3;
    }

    @Override
    public boolean hasTexture() {
        return texturePath != null && textureCoords != null && textureCoords.length > 0;
    }

    @Override
    public boolean hasNormals() {
        return normals != null && normals.length > 0;
    }

    public int createUniqueId() {
        return uniqueIdCounter++;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getId() {
        return id;
    }

    public boolean is2D() {
        return is2D;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public int getTextureId() {
        return textureId;
    }

    public boolean isEquals(OGLModelData modelData) {
        int comparisonId = modelData == null ? -1 : modelData.id;
        int comparisonTextureId = modelData == null ? -1 : modelData.textureId;
        return id == comparisonId && textureId == comparisonTextureId;
    }

    public boolean process2D() {
        for (int i = 0; i < vertices.length; i += 3) {
            if (vertices[i + 2] != 0) {
                return false;
            }
        }
        return true;
    }

    public static void reset() {
        uniqueIdCounter = 0;
        previousModel = null;
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