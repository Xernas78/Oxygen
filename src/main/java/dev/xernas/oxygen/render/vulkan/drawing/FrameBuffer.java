package dev.xernas.oxygen.render.vulkan.drawing;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class FrameBuffer implements IOxygenLogic {

    private final Device device;
    private final int width;
    private final int height;
    private final LongBuffer pAttachments;
    private final long renderPass;
    private long vkFrameBuffer;

    public FrameBuffer(Device device, int width, int height, LongBuffer pAttachments, long renderPass) {
        this.device = device;
        this.width = width;
        this.height = height;
        this.pAttachments = pAttachments;
        this.renderPass = renderPass;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pAttachments(pAttachments)
                    .width(width)
                    .height(height)
                    .layers(1)
                    .renderPass(renderPass);

            LongBuffer lp = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateFramebuffer(device.getVkDevice(), fci, null, lp),
                    "Failed to create FrameBuffer");
            vkFrameBuffer = lp.get(0);
        }
    }

    public void cleanup() {
        vkDestroyFramebuffer(device.getVkDevice(), vkFrameBuffer, null);
    }

    public long getVkFrameBuffer() {
        return vkFrameBuffer;
    }

}
