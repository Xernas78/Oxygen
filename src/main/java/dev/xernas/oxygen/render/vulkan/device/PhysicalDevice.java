package dev.xernas.oxygen.render.vulkan.device;

import dev.xernas.atom.list.ListUtils;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.Instance;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevice implements IOxygenLogic {

    private final Instance instance;

    private VkPhysicalDevice vkPhysicalDevice;
    private VkExtensionProperties.Buffer vkDeviceExtensions;
    private VkPhysicalDeviceMemoryProperties vkMemoryProperties;
    private VkPhysicalDeviceFeatures vkPhysicalDeviceFeatures;
    private VkPhysicalDeviceProperties vkPhysicalDeviceProperties;
    private VkQueueFamilyProperties.Buffer vkQueueFamilyProps;


    public PhysicalDevice(Instance instance) {
        this.instance = instance;
    }

    public PhysicalDevice(Instance instance, VkPhysicalDevice vkPhysicalDevice) {
        this.instance = instance;
        this.vkPhysicalDevice = vkPhysicalDevice;
    }

    public VkPhysicalDevice findVkPhysicalDevice(MemoryStack stack) throws OxygenException {
        PhysicalDevice selectedDevice = null;
        IntBuffer physicalDeviceCount = stack.mallocInt(1);
        VulkanUtils.vkCheck(vkEnumeratePhysicalDevices(instance.getVkInstance(), physicalDeviceCount, null), "Failed to enumerate physical devices");
        int numPhysicalDevices = physicalDeviceCount.get(0);
        if (numPhysicalDevices == 0) throw new OxygenException("Failed to find GPUs with Vulkan support");
        PointerBuffer pPhysicalDevices = stack.mallocPointer(numPhysicalDevices);
        VulkanUtils.vkCheck(vkEnumeratePhysicalDevices(instance.getVkInstance(), physicalDeviceCount, pPhysicalDevices), "Failed to enumerate physical devices");
        List<PhysicalDevice> potentialDevices = new ArrayList<>();
        for (int i = 0; i < numPhysicalDevices; i++) {
            VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(i), instance.getVkInstance());
            PhysicalDevice physicalDevice = new PhysicalDevice(instance, vkPhysicalDevice);
            physicalDevice.init();

            if (physicalDevice.hasGraphicsQueueFamily() && physicalDevice.hasKHRSwapChainExtension()) {
                potentialDevices.add(physicalDevice);
            }
            else {
                physicalDevice.cleanup();
            }
        }

        selectedDevice = !potentialDevices.isEmpty() ? ListUtils.getRandomValue(potentialDevices) : selectedDevice;

        potentialDevices.remove(selectedDevice);

        for (PhysicalDevice potentialDevice : potentialDevices) {
            potentialDevice.cleanup();
        }

        if (selectedDevice == null) {
            throw new OxygenException("Failed to find a suitable GPU");
        }
        return selectedDevice.getVkPhysicalDevice();
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            if (vkPhysicalDevice == null) {
                vkPhysicalDevice = findVkPhysicalDevice(stack);
            }

            IntBuffer pCount = stack.mallocInt(1);

            //Properties
            vkPhysicalDeviceProperties = VkPhysicalDeviceProperties.calloc();
            vkGetPhysicalDeviceProperties(vkPhysicalDevice, vkPhysicalDeviceProperties);

            //Device extensions
            VulkanUtils.vkCheck(vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, pCount, null),
                    "Failed to get number of device extension properties");
            vkDeviceExtensions = VkExtensionProperties.calloc(pCount.get(0));
            VulkanUtils.vkCheck(vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, pCount, vkDeviceExtensions),
                    "Failed to get extension properties");

            //Queue family properties
            vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, pCount, null);
            vkQueueFamilyProps = VkQueueFamilyProperties.calloc(pCount.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, pCount, vkQueueFamilyProps);

            vkPhysicalDeviceFeatures = VkPhysicalDeviceFeatures.calloc();
            vkGetPhysicalDeviceFeatures(vkPhysicalDevice, vkPhysicalDeviceFeatures);

            //Memory information and properties
            vkMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            vkGetPhysicalDeviceMemoryProperties(vkPhysicalDevice, vkMemoryProperties);
        }
    }

    public void cleanup() {
        vkMemoryProperties.free();
        vkPhysicalDeviceFeatures.free();
        vkQueueFamilyProps.free();
        vkDeviceExtensions.free();
        vkPhysicalDeviceProperties.free();
    }

    private boolean hasKHRSwapChainExtension() {
        boolean result = false;
        int numExtensions = vkDeviceExtensions != null ? vkDeviceExtensions.capacity() : 0;
        for (int i = 0; i < numExtensions; i++) {
            String extensionName = vkDeviceExtensions.get(i).extensionNameString();
            if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(extensionName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean hasGraphicsQueueFamily() {
        boolean result = false;
        int numQueueFamilies = vkQueueFamilyProps != null ? vkQueueFamilyProps.capacity() : 0;
        for (int i = 0; i < numQueueFamilies; i++) {
            VkQueueFamilyProperties familyProps = vkQueueFamilyProps.get(i);
            if ((familyProps.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    public String getDeviceName() {
        return vkPhysicalDeviceProperties.deviceNameString();
    }

    public VkPhysicalDeviceMemoryProperties getVkMemoryProperties() {
        return vkMemoryProperties;
    }

    public VkPhysicalDevice getVkPhysicalDevice() {
        return vkPhysicalDevice;
    }

    public VkPhysicalDeviceFeatures getVkPhysicalDeviceFeatures() {
        return vkPhysicalDeviceFeatures;
    }

    public VkPhysicalDeviceProperties getVkPhysicalDeviceProperties() {
        return vkPhysicalDeviceProperties;
    }

    public VkQueueFamilyProperties.Buffer getVkQueueFamilyProps() {
        return vkQueueFamilyProps;
    }

}
