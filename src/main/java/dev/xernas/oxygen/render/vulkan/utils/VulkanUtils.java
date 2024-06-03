package dev.xernas.oxygen.render.vulkan.utils;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import org.lwjgl.vulkan.VkMemoryType;

import java.util.Locale;

import static org.lwjgl.vulkan.VK10.VK_MAX_MEMORY_TYPES;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanUtils {

    public static int memoryTypeFromProperties(PhysicalDevice physDevice, int typeBits, int reqsMask) {
        int result = -1;
        VkMemoryType.Buffer memoryTypes = physDevice.getVkMemoryProperties().memoryTypes();
        for (int i = 0; i < VK_MAX_MEMORY_TYPES; i++) {
            if ((typeBits & 1) == 1 && (memoryTypes.get(i).propertyFlags() & reqsMask) == reqsMask) {
                result = i;
                break;
            }
            typeBits >>= 1;
        }
        if (result < 0) {
            throw new RuntimeException("Failed to find memoryType");
        }
        return result;
    }

    public static OSType getOS() {
        OSType result;
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((os.contains("mac")) || (os.contains("darwin"))) {
            result = OSType.MACOS;
        } else if (os.contains("win")) {
            result = OSType.WINDOWS;
        } else if (os.contains("nux")) {
            result = OSType.LINUX;
        } else {
            result = OSType.OTHER;
        }

        return result;
    }

    public static void vkCheck(int err, String errMsg) throws OxygenException {
        if (err != VK_SUCCESS) {
            throw new OxygenException(errMsg + ": " + err);
        }
    }

    public enum OSType {
        WINDOWS,
        LINUX,
        MACOS,
        OTHER
    }

}
