package dev.xernas.oxygen.render.vulkan.command;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;

public class GraphicsQueue extends Queue {

    private final Device device;

    public GraphicsQueue(Device device, int queueIndex) {
        super(device, queueIndex);
        this.device = device;
    }

    @Override
    public void init() throws OxygenException {
        super.init();
        setQueueFamilyIndex(getGraphicsQueueFamilyIndex(device));
    }

    private static int getGraphicsQueueFamilyIndex(Device device) {
        int index = -1;
        PhysicalDevice physicalDevice = device.getPhysicalDevice();
        VkQueueFamilyProperties.Buffer queuePropsBuff = physicalDevice.getVkQueueFamilyProps();
        int numQueuesFamilies = queuePropsBuff.capacity();
        for (int i = 0; i < numQueuesFamilies; i++) {
            VkQueueFamilyProperties props = queuePropsBuff.get(i);
            boolean graphicsQueue = (props.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0;
            if (graphicsQueue) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            throw new RuntimeException("Failed to get graphics Queue family index");
        }
        return index;
    }

}
