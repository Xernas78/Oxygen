package dev.xernas.oxygen.render.oxygen.model;

import dev.xernas.oxygen.engine.material.Material;

public class Model {

    public static final Model CUBE;

    static {
        CUBE = new Model(new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
                3, 2, 7,
                7, 2, 6,
                7, 6, 4,
                4, 6, 5,
                4, 5, 0,
                0, 5, 1,
                4, 0, 7,
                7, 0, 3,
                1, 5, 2,
                2, 5, 6
        }, null, null, null);
    }

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private Material material;

    public Model(float[] vertices, int[] indices, float[] normals, float[] textureCoords, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.material = material == null ? Material.DEFAULT : material;
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

    public Material getMaterial() {
        return material;
    }

    public Model setMaterial(Material material) {
        this.material = material;
        return this;
    }
}
