package dev.xernas.oxygen.engine.model;

import dev.xernas.oxygen.engine.material.DefaultMaterial;
import dev.xernas.oxygen.engine.material.Material;
import org.jetbrains.annotations.NotNull;

public class Model {

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private final boolean is2D;
    private Material material;

    public Model(float[] vertices, int[] indices, float[] normals, float[] textureCoords, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.material = material == null ? new DefaultMaterial() : material;
        this.is2D = process2D();
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

    public boolean process2D() {
        for (int i = 0; i < vertices.length; i += 3) {
            if (vertices[i + 2] != 0) {
                return false;
            }
        }
        return true;
    }

    public Material getMaterial() {
        return material;
    }

    public Model setMaterial(@NotNull Material material) {
        this.material = material;
        return this;
    }

    public Model copy() {
        return new Model(vertices, indices, normals, textureCoords, material);
    }
}
