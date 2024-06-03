package dev.xernas.oxygen.render.vulkan.device;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class Device implements IOxygenLogic {

    private final PhysicalDevice physicalDevice;
    private VkDevice vkDevice;

    public Device(PhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()){
            Set<String> deviceExtensions = getDeviceExtensions();
            boolean usePortability = deviceExtensions.contains(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME) && VulkanUtils.getOS() == VulkanUtils.OSType.MACOS;
            int numExtensions = usePortability ? 2 : 1;
            PointerBuffer pRequiredExtensions = stack.mallocPointer(numExtensions);
            pRequiredExtensions.put(stack.ASCII(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));
            if (usePortability) {
                pRequiredExtensions.put(stack.ASCII(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME));
            }
            pRequiredExtensions.flip();

            VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);

            //Enable all the queue families
            VkQueueFamilyProperties.Buffer queuePropsBuff = physicalDevice.getVkQueueFamilyProps();
            int numQueuesFamilies = queuePropsBuff.capacity();
            VkDeviceQueueCreateInfo.Buffer queueCreationInfoBuf = VkDeviceQueueCreateInfo.calloc(numQueuesFamilies, stack);
            for (int i = 0; i < numQueuesFamilies; i++) {
                FloatBuffer priorities = stack.callocFloat(queuePropsBuff.get(i).queueCount());
                queueCreationInfoBuf.get(i)
                        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(i)
                        .pQueuePriorities(priorities);
            }

            //Create the logical device
            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .ppEnabledExtensionNames(pRequiredExtensions)
                    .pEnabledFeatures(features)
                    .pQueueCreateInfos(queueCreationInfoBuf);

            PointerBuffer pDevice = stack.mallocPointer(1);
            VulkanUtils.vkCheck(vkCreateDevice(physicalDevice.getVkPhysicalDevice(), deviceCreateInfo, null, pDevice),
                    "Failed to create device");
            vkDevice = new VkDevice(pDevice.get(0), physicalDevice.getVkPhysicalDevice(), deviceCreateInfo);
        }
    }

    public void cleanup() {
        vkDestroyDevice(vkDevice, null);
    }

    private Set<String> getDeviceExtensions() {
        Set<String> deviceExtensions = new HashSet<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pNumExtensionsBuf = stack.callocInt(1);
            vkEnumerateDeviceExtensionProperties(physicalDevice.getVkPhysicalDevice(), (String) null, pNumExtensionsBuf, null);
            int numExtensions = pNumExtensionsBuf.get(0);

            VkExtensionProperties.Buffer propsBuff = VkExtensionProperties.calloc(numExtensions, stack);
            vkEnumerateDeviceExtensionProperties(physicalDevice.getVkPhysicalDevice(), (String) null, pNumExtensionsBuf, propsBuff);
            for (int i = 0; i < numExtensions; i++) {
                VkExtensionProperties props = propsBuff.get(i);
                String extensionName = props.extensionNameString();
                deviceExtensions.add(extensionName);
            }
        }
        return deviceExtensions;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public VkDevice getVkDevice() {
        return vkDevice;
    }

    public void waitIdle() {
        vkDeviceWaitIdle(vkDevice);
    }

}
