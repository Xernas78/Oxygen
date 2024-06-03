package dev.xernas.oxygen.render.vulkan.buffers;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanBuffer implements IOxygenLogic {

    private final Device device;
    private final long requestedSize;
    private final int usage;
    private final int reqMask;

    private long allocationSize;
    private long buffer;
    private long memory;
    private PointerBuffer pointerBuffer;
    private long mappedMemory;

    public VulkanBuffer(Device device, long requestedSize, int usage, int reqMask) {
        this.device = device;
        this.requestedSize = requestedSize;
        this.usage = usage;
        this.reqMask = reqMask;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                    .size(requestedSize)
                    .usage(usage)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer pBuffer = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateBuffer(device.getVkDevice(), bufferCreateInfo, null, pBuffer), "Failed to create buffer");
            buffer = pBuffer.get(0);

            VkMemoryRequirements memReqs = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(device.getVkDevice(), buffer, memReqs);

            VkMemoryAllocateInfo memAlloc = VkMemoryAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    .allocationSize(memReqs.size())
                    .memoryTypeIndex(VulkanUtils.memoryTypeFromProperties(device.getPhysicalDevice(),
                            memReqs.memoryTypeBits(), reqMask));

            VulkanUtils.vkCheck(vkAllocateMemory(device.getVkDevice(), memAlloc, null, pBuffer), "Failed to allocate memory");
            allocationSize = memAlloc.allocationSize();
            memory = pBuffer.get(0);
            pointerBuffer = MemoryUtil.memAllocPointer(1);

            VulkanUtils.vkCheck(vkBindBufferMemory(device.getVkDevice(), buffer, memory, 0), "Failed to bind buffer memory");
        }
    }

    @Override
    public void cleanup() throws OxygenException {
        MemoryUtil.memFree(pointerBuffer);
        vkDestroyBuffer(device.getVkDevice(), buffer, null);
        vkFreeMemory(device.getVkDevice(), memory, null);
    }

    public long getBuffer() {
        return buffer;
    }

    public long getRequestedSize() {
        return requestedSize;
    }

    public long map() throws OxygenException {
        if (mappedMemory == NULL) {
            VulkanUtils.vkCheck(vkMapMemory(device.getVkDevice(), memory, 0, allocationSize, 0, pointerBuffer), "Failed to map Buffer");
            mappedMemory = pointerBuffer.get(0);
        }
        return mappedMemory;
    }

    public void unMap() {
        if (mappedMemory != NULL) {
            vkUnmapMemory(device.getVkDevice(), memory);
            mappedMemory = NULL;
        }
    }

}
