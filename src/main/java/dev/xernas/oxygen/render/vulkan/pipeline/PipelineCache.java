package dev.xernas.oxygen.render.vulkan.pipeline;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.buffers.VertexInputStateInfo;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.shader.ShaderProgram;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class PipelineCache implements IOxygenLogic {

    private final Device device;
    private long vkPipelineCache;

    public PipelineCache(Device device) {
        this.device = device;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineCacheCreateInfo createInfo = VkPipelineCacheCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_CACHE_CREATE_INFO);

            LongBuffer lp = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreatePipelineCache(device.getVkDevice(), createInfo, null, lp),
                    "Error creating pipeline cache");
            vkPipelineCache = lp.get(0);
        }
    }

    public void cleanup() {
        vkDestroyPipelineCache(device.getVkDevice(), vkPipelineCache, null);
    }

    public Device getDevice() {
        return device;
    }

    public long getVkPipelineCache() {
        return vkPipelineCache;
    }

    public record PipeLineCreationInfo(long vkRenderPass, ShaderProgram shaderProgram, int numColorAttachments, VertexInputStateInfo viInputStateInfo) {

        public void cleanup() {
            viInputStateInfo.cleanup();
        }

    }

}
