package dev.xernas.oxygen.render.vulkan.sync;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class Semaphore implements IOxygenLogic {

    private final Device device;
    private long vkSemaphore;

    public Semaphore(Device device){
        this.device = device;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo pSemaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            LongBuffer pSemaphore = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateSemaphore(device.getVkDevice(), pSemaphoreCreateInfo, null, pSemaphore),
                    "Failed to create semaphore");
            vkSemaphore = pSemaphore.get(0);
        }
    }

    public void cleanup() {
        vkDestroySemaphore(device.getVkDevice(), vkSemaphore, null);
    }

    public long getVkSemaphore() {
        return vkSemaphore;
    }

}
