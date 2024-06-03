package dev.xernas.oxygen.render.vulkan.buffers;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.utils.VulkanConstants;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import static org.lwjgl.vulkan.VK10.*;

public class VertexBufferStructure extends VertexInputStateInfo {

    private static final int NUMBER_OF_ATTRIBUTES = 1;
    private static final int POSITION_COMPONENTS = 3;

    private VkVertexInputAttributeDescription.Buffer viAttrs;
    private VkVertexInputBindingDescription.Buffer viBindings;

    @Override
    public void init() throws OxygenException {
        viAttrs = VkVertexInputAttributeDescription.calloc(NUMBER_OF_ATTRIBUTES);
        viBindings = VkVertexInputBindingDescription.calloc(1);
        vi = VkPipelineVertexInputStateCreateInfo.calloc();

        int i = 0;
        // Position
        viAttrs.get(i)
                .binding(0)
                .location(i)
                .format(VK_FORMAT_R32G32B32_SFLOAT)
                .offset(0);

        viBindings.get(0)
                .binding(0)
                .stride(POSITION_COMPONENTS * VulkanConstants.FLOAT_LENGTH)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        vi
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pVertexBindingDescriptions(viBindings)
                .pVertexAttributeDescriptions(viAttrs);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        viBindings.free();
        viAttrs.free();
    }

}
