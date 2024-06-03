package dev.xernas.oxygen.render.vulkan.model;

import dev.xernas.oxygen.exception.OxygenException;
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

public class Model {

    private final String modelId;
    private final List<Mesh> meshList;

    public Model(String modelId) {
        this.modelId = modelId;
        meshList = new ArrayList<>();
    }

    public void cleanup() throws OxygenException {
        for (Mesh mesh : meshList) {
            mesh.cleanup();
        }
    }

    public static List<Model> transformModels(List<ModelData> modelDataList, CommandPool commandPool, Queue queue) throws OxygenException {
        List<Model> vulkanModelList = new ArrayList<>();
        Device device = commandPool.getDevice();
        CommandBuffer cmd = new CommandBuffer(commandPool, true, true);
        device.init();
        cmd.init();
        List<VulkanBuffer> stagingBufferList = new ArrayList<>();

        cmd.beginRecording();
        for (ModelData modelData : modelDataList) {
            Model vulkanModel = new Model(modelData.getModelId());
            vulkanModelList.add(vulkanModel);

            // Transform meshes loading their data into GPU buffers
            for (ModelData.MeshData meshData : modelData.getMeshDataList()) {
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

        return vulkanModelList;
    }

    private static void recordTransferCommand(CommandBuffer cmd, TransferBuffers transferBuffers) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack)
                    .srcOffset(0).dstOffset(0).size(transferBuffers.srcBuffer().getRequestedSize());
            vkCmdCopyBuffer(cmd.getVkCommandBuffer(), transferBuffers.srcBuffer().getBuffer(),
                    transferBuffers.dstBuffer().getBuffer(), copyRegion);
        }
    }

    private static TransferBuffers createIndicesBuffers(Device device, ModelData.MeshData meshData) throws OxygenException {
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

    private static TransferBuffers createVerticesBuffers(Device device, ModelData.MeshData meshData) throws OxygenException {
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


    public String getModelId() {
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
