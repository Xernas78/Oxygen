package dev.xernas.oxygen.render.vulkan.buffers;

import dev.xernas.oxygen.IOxygenLogic;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;

public abstract class VertexInputStateInfo implements IOxygenLogic {

    protected VkPipelineVertexInputStateCreateInfo vi;

    public void cleanup() {
        vi.free();
    }

    public VkPipelineVertexInputStateCreateInfo getVi() {
        return vi;
    }

}
