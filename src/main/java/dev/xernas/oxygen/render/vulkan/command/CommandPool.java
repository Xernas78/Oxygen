package dev.xernas.oxygen.render.vulkan.command;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class CommandPool implements IOxygenLogic {

    private final Device device;
    private final int queueFamilyIndex;

    private long vkCommandPool;

    public CommandPool(Device device, int queueFamilyIndex) {
        this.device = device;
        this.queueFamilyIndex = queueFamilyIndex;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo pCmdPoolInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                    .queueFamilyIndex(queueFamilyIndex);

            LongBuffer pCommandPool = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateCommandPool(device.getVkDevice(), pCmdPoolInfo, null, pCommandPool),
                    "Failed to create command pool");

            vkCommandPool = pCommandPool.get(0);
        }
    }

    public void cleanup() {
        vkDestroyCommandPool(device.getVkDevice(), vkCommandPool, null);
    }

    public Device getDevice() {
        return device;
    }

    public long getVkCommandPool() {
        return vkCommandPool;
    }

}
