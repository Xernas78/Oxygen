package dev.xernas.oxygen.render.vulkan.model;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModel;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModelData;
import dev.xernas.oxygen.render.vulkan.buffers.VulkanBuffer;
import dev.xernas.oxygen.render.vulkan.command.CommandBuffer;
import dev.xernas.oxygen.render.vulkan.command.CommandPool;
import dev.xernas.oxygen.render.vulkan.command.Queue;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.sync.Fence;
import dev.xernas.oxygen.render.vulkan.utils.VulkanConstants;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCopy;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanModel implements IModel {

    private final Integer modelId;
    private final List<Mesh> meshList;

    public VulkanModel(Integer modelId) {
        this.modelId = modelId;
        meshList = new ArrayList<>();
    }

    @Override
    public void init() throws OxygenException {

    }

    @Override
    public void cleanup() throws OxygenException {
        for (Mesh mesh : meshList) {
            mesh.cleanup();
        }
    }

    public static List<VulkanModel> transformModels(List<IModelData> modelDataList, CommandPool commandPool, Queue queue) throws OxygenException {
        List<VulkanModel> vulkanVulkanModelList = new ArrayList<>();
        Device device = commandPool.getDevice();
        CommandBuffer cmd = new CommandBuffer(commandPool, true, true);
        device.init();
        cmd.init();
        List<VulkanBuffer> stagingBufferList = new ArrayList<>();

        cmd.beginRecording();
        for (IModelData iModelData : modelDataList) {
            VulkanModelData vulkanModelData = (VulkanModelData) iModelData;
            VulkanModel vulkanModel = new VulkanModel(vulkanModelData.getModelId());
            vulkanVulkanModelList.add(vulkanModel);

            // Transform meshes loading their data into GPU buffers
            for (VulkanModelData.MeshData meshData : vulkanModelData.getMeshDataList()) {
                TransferBuffers verticesBuffers = createVerticesBuffers(device, meshData);
                TransferBuffers indicesBuffers = createIndicesBuffers(device, meshData);
                stagingBufferList.add(verticesBuffers.srcBuffer());
                stagingBufferList.add(indicesBuffers.srcBuffer());
                recordTransferCommand(cmd, verticesBuffers);
                recordTransferCommand(cmd, indicesBuffers);

                Mesh mesh = new Mesh(verticesBuffers.dstBuffer(),
                        indicesBuffers.dstBuffer(), meshData.indices().length);

                vulkanModel.meshList.add(mesh);
            }
        }

        cmd.endRecording();
        Fence fence = new Fence(device, true);
        fence.init();
        fence.reset();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            queue.submit(stack.pointers(cmd.getVkCommandBuffer()), null, null, null, fence);
        }
        fence.fenceWait();
        fence.cleanup();
        cmd.cleanup();

        for (VulkanBuffer vulkanBuffer : stagingBufferList) {
            vulkanBuffer.cleanup();
        }

        return vulkanVulkanModelList;
    }

    public static VulkanModel transformModel(IModelData modelData, CommandPool commandPool, Queue queue) throws OxygenException {
        List<IModelData> modelDataList = new ArrayList<>();
        modelDataList.add(modelData);
        return transformModels(modelDataList, commandPool, queue).get(0);
    }

    private static void recordTransferCommand(CommandBuffer cmd, TransferBuffers transferBuffers) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack)
                    .srcOffset(0).dstOffset(0).size(transferBuffers.srcBuffer().getRequestedSize());
            vkCmdCopyBuffer(cmd.getVkCommandBuffer(), transferBuffers.srcBuffer().getBuffer(),
                    transferBuffers.dstBuffer().getBuffer(), copyRegion);
        }
    }

    private static TransferBuffers createIndicesBuffers(Device device, VulkanModelData.MeshData meshData) throws OxygenException {
        int[] indices = meshData.indices();
        int numIndices = indices.length;
        int bufferSize = numIndices * VulkanConstants.INT_LENGTH;

        VulkanBuffer srcBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        VulkanBuffer dstBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
        srcBuffer.init();
        dstBuffer.init();

        long mappedMemory = srcBuffer.map();
        IntBuffer data = MemoryUtil.memIntBuffer(mappedMemory, (int) srcBuffer.getRequestedSize());
        data.put(indices);
        srcBuffer.unMap();

        return new TransferBuffers(srcBuffer, dstBuffer);
    }

    private static TransferBuffers createVerticesBuffers(Device device, VulkanModelData.MeshData meshData) throws OxygenException {
        float[] positions = meshData.vertices();
        int numPositions = positions.length;
        int bufferSize = numPositions * VulkanConstants.FLOAT_LENGTH;

        VulkanBuffer srcBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        VulkanBuffer dstBuffer = new VulkanBuffer(device, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
        srcBuffer.init();
        dstBuffer.init();

        long mappedMemory = srcBuffer.map();
        FloatBuffer data = MemoryUtil.memFloatBuffer(mappedMemory, (int) srcBuffer.getRequestedSize());
        data.put(positions);
        srcBuffer.unMap();

        return new TransferBuffers(srcBuffer, dstBuffer);
    }

    @Override
    public Integer getModelId() {
        return modelId;
    }

    public List<Mesh> getVulkanMeshList() {
        return meshList;
    }

    public record Mesh(VulkanBuffer verticesBuffer, VulkanBuffer indicesBuffer, int numIndices) {

        public void cleanup() throws OxygenException {
            verticesBuffer.cleanup();
            indicesBuffer.cleanup();
        }
    }

    private record TransferBuffers(VulkanBuffer srcBuffer, VulkanBuffer dstBuffer) {

    }

}
