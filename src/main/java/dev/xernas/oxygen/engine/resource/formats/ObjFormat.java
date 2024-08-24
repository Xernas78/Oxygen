package dev.xernas.oxygen.engine.resource.formats;

import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.resource.IFormat;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.engine.model.Model;

public class ObjFormat implements IFormat {

    private final ResourceManager resourceManager;
    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;
    private final float[] normals;
    private final int faceCount;
    private final boolean textured;

    public ObjFormat(ResourceManager resourceManager, float[] vertices, int[] indices, float[] texCoords, float[] normals, int faceCount, boolean textured) {
        this.resourceManager = resourceManager;
        this.vertices = vertices;
        this.indices = indices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.faceCount = faceCount;
        this.textured = textured;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int getNumVertices() {
        return vertices.length / 3;
    }

    public int getNumFaces() {
        return faceCount;
    }

    @Override
    public Model toModel(Material material) {
        Material newMaterial = material == null ? Material.DEFAULT : new Material(material, textured);
        return new Model(vertices, indices, texCoords, normals, newMaterial);
    }

    @Override
    public Model toModel() {
        return new Model(vertices, indices, texCoords, normals, null);
    }

}
