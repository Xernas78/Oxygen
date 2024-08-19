package dev.xernas.oxygen.engine.model;

import dev.xernas.oxygen.engine.material.Material;

public class Model {

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

    public Model copy() {
        return new Model(vertices, indices, normals, textureCoords, material);
    }
}
