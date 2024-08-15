package dev.xernas.oxygen.render.vulkan.model;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModelData;

import java.util.List;

public class VulkanModelData implements IModelData {

    private final List<MeshData> meshDataList;
    private final Integer modelId;

    public VulkanModelData(Integer modelId, List<MeshData> meshDataList) {
        this.modelId = modelId;
        this.meshDataList = meshDataList;
    }

    public List<MeshData> getMeshDataList() {
        return meshDataList;
    }

    public Integer getModelId() {
        return modelId;
    }

    @Override
    public void init() throws OxygenException {

    }

    @Override
    public void cleanup() throws OxygenException {

    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public boolean hasTexture() {
        return false;
    }

    @Override
    public boolean hasNormals() {
        return false;
    }

    public record MeshData(float[] vertices, int[] indices) {

    }

}
