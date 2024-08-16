package dev.xernas.oxygen.engine.resource.obj;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.render.oxygen.model.Model;

public class ObjFormat {

    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;
    private final float[] normals;
    private final int faceCount;
    private final String objName;

    public ObjFormat(float[] vertices, int[] indices, float[] texCoords, float[] normals, int faceCount, String objName) {
        this.vertices = vertices;
        this.indices = indices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.faceCount = faceCount;
        this.objName = objName;
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

    public Model toModel(Material material) {
        String textureFilePath = Oxygen.OXYGEN_RESOURCE_MANAGER.getFileResourceAbsolutePath("models/" + objName + "/" + material.getTexturePath());
        Material newMaterial = new Material(
                objName == null || objName.isEmpty() ? null : (textureFilePath == null ? Oxygen.OXYGEN_RESOURCE_MANAGER.getFileResourceAbsolutePath("textures/error.png") : textureFilePath),
                material.getBaseColor(),
                material.illuminable(),
                material.getShininess(),
                material.getShineDamper());
        return new Model(vertices, indices, texCoords, normals, newMaterial);
    }

    public Model toModel() {
        return new Model(vertices, indices, texCoords, normals, null);
    }

}
