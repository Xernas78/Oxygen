package dev.xernas.oxygen.render.vulkan.sync;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class Fence implements IOxygenLogic {

    private final Device device;
    private final boolean signaled;
    private long vkFence;

    public Fence(Device device, boolean signaled) {
        this.device = device;
        this.signaled = signaled;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo pFenceCreateInfo = VkFenceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer pFence = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateFence(device.getVkDevice(), pFenceCreateInfo, null, pFence),
                    "Failed to create semaphore");
            vkFence = pFence.get(0);
        }
    }

    public void cleanup() {
        vkDestroyFence(device.getVkDevice(), vkFence, null);
    }

    public void fenceWait() {
        vkWaitForFences(device.getVkDevice(), vkFence, true, Long.MAX_VALUE);
    }

    public long getVkFence() {
        return vkFence;
    }

    public void reset() {
        vkResetFences(device.getVkDevice(), vkFence);
    }

}
