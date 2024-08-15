package dev.xernas.oxygen.render.oxygen.model;

public class Model {

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private final String texturePath;

    public Model(float[] vertices, int[] indices, float[] normals, float[] textureCoords, String texturePath) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.texturePath = texturePath;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public String getTexturePath() {
        return texturePath;
    }
}
