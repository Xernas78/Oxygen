package dev.xernas.oxygen.render.vulkan.command;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import dev.xernas.oxygen.render.vulkan.drawing.surface.Surface;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.VK_TRUE;

public class PresentQueue extends Queue {

    private final Device device;
    private final Surface surface;

    public PresentQueue(Device device, Surface surface, int queueIndex) {
        super(device, queueIndex);
        this.device = device;
        this.surface = surface;
    }

    @Override
    public void init() throws OxygenException {
        super.init();
        setQueueFamilyIndex(getPresentQueueFamilyIndex(device, surface));
    }

    private static int getPresentQueueFamilyIndex(Device device, Surface surface) throws OxygenException {
        int index = -1;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PhysicalDevice physicalDevice = device.getPhysicalDevice();
            VkQueueFamilyProperties.Buffer queuePropsBuff = physicalDevice.getVkQueueFamilyProps();
            int numQueuesFamilies = queuePropsBuff.capacity();
            IntBuffer intBuff = stack.mallocInt(1);
            for (int i = 0; i < numQueuesFamilies; i++) {
                KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.getVkPhysicalDevice(),
                        i, surface.getVkSurface(), intBuff);
                boolean supportsPresentation = intBuff.get(0) == VK_TRUE;
                if (supportsPresentation) {
                    index = i;
                    break;
                }
            }
        }

        if (index < 0) {
            throw new OxygenException("Failed to get Presentation Queue family index");
        }
        return index;
    }
}
