package dev.xernas.oxygen.render.vulkan.command;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.sync.Fence;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class Queue implements IOxygenLogic {

    private final Device device;
    private final int queueIndex;

    private int queueFamilyIndex;
    private VkQueue vkQueue;

    public Queue(Device device, int queueIndex) {
        this.device = device;
        this.queueIndex = queueIndex;
    }

    public Queue(Device device, int queueFamilyIndex, int queueIndex) {
        this.device = device;
        this.queueFamilyIndex = queueFamilyIndex;
        this.queueIndex = queueIndex;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device.getVkDevice(), queueFamilyIndex, queueIndex, pQueue);
            long queue = pQueue.get(0);
            vkQueue = new VkQueue(queue, device.getVkDevice());
        }
    }

    public void submit(PointerBuffer commandBuffers, LongBuffer waitSemaphores, IntBuffer dstStageMasks,
                       LongBuffer signalSemaphores, Fence fence) throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo pSubmitInfo = VkSubmitInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(commandBuffers)
                    .pSignalSemaphores(signalSemaphores);
            if (waitSemaphores != null) {
                pSubmitInfo.waitSemaphoreCount(waitSemaphores.capacity())
                        .pWaitSemaphores(waitSemaphores)
                        .pWaitDstStageMask(dstStageMasks);
            } else {
                pSubmitInfo.waitSemaphoreCount(0);
            }
            long fenceHandle = fence != null ? fence.getVkFence() : VK_NULL_HANDLE;

            VulkanUtils.vkCheck(vkQueueSubmit(vkQueue, pSubmitInfo, fenceHandle),
                    "Failed to submit command to queue");
        }
    }

    public void setQueueFamilyIndex(int queueFamilyIndex) {
        this.queueFamilyIndex = queueFamilyIndex;
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public VkQueue getVkQueue() {
        return vkQueue;
    }

    public void waitIdle() {
        vkQueueWaitIdle(vkQueue);
    }

    @Override
    public void cleanup() throws OxygenException {

    }
}
