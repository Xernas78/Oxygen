package dev.xernas.oxygen.render.vulkan;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.exception.VulkanException;
import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class Instance implements IOxygenLogic {

    public static final int MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
    public static final int MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
    private static final String PORTABILITY_EXTENSION = "VK_KHR_portability_enumeration";

    private VkInstance vkInstance;
    private final String appName;
    private final String version;
    private final boolean validate;

    private VkDebugUtilsMessengerCreateInfoEXT debugUtils;
    private long vkDebugHandle;

    public Instance(String appName, String version, boolean validate) {
        this.appName = appName;
        this.version = version;
        this.validate = validate;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            //Application Info
            String[] versionParts = version.split("\\.");
            int major = Integer.parseInt(versionParts[0]);
            int minor = Integer.parseInt(versionParts[1]);
            int patch = Integer.parseInt(versionParts[2]);
            ByteBuffer bufferAppName = stack.UTF8(appName);
            int vkVersion = VK_MAKE_VERSION(major, minor, patch);
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType$Default()
                    .pNext(0)
                    .pApplicationName(bufferAppName)
                    .applicationVersion(vkVersion)
                    .pEngineName(bufferAppName)
                    .engineVersion(vkVersion)
                    .apiVersion(VK_API_VERSION_1_3); //Potentielle source d'erreurs

            //Validation Layers
            List<String> validationLayers = getSupportedValidationLayers(stack);
            int numLayers = validationLayers.size();
            boolean supportsValidation = validate;
            if (validate && numLayers == 0) {
                supportsValidation = false;
                Oxygen.LOGGER.warn("Validation layers requested but not available");
            }

            PointerBuffer pRequiredLayers = null;
            if (supportsValidation) {
                pRequiredLayers = stack.mallocPointer(numLayers);
                for (int i = 0; i < numLayers; i++) {
                    pRequiredLayers.put(i, stack.ASCII(validationLayers.get(i)));
                }
            }

            //Extensions
            PointerBuffer pGlfwRequiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            if (pGlfwRequiredExtensions == null) {
                Oxygen.LOGGER.fatal(new VulkanException("Failed to get required extensions from GLFW"));
            }

            Set<String> instanceExtensions = getInstanceExtensions(stack);
            boolean usePortability = instanceExtensions.contains(PORTABILITY_EXTENSION) &&
                    VulkanUtils.getOS() == VulkanUtils.OSType.MACOS;
            PointerBuffer pRequiredExtensions;
            if (supportsValidation) {
                ByteBuffer vkDebugUtilsExtension = stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
                int numExtensions = usePortability ? pGlfwRequiredExtensions.remaining() + 2 : pGlfwRequiredExtensions.remaining() + 1;
                pRequiredExtensions = stack.mallocPointer(numExtensions);
                pRequiredExtensions.put(pGlfwRequiredExtensions).put(vkDebugUtilsExtension);
                if (usePortability) {
                    pRequiredExtensions.put(stack.UTF8(PORTABILITY_EXTENSION));
                }
            } else {
                int numExtensions = usePortability ? pGlfwRequiredExtensions.remaining() + 1 : pGlfwRequiredExtensions.remaining();
                pRequiredExtensions = stack.mallocPointer(numExtensions);
                pRequiredExtensions.put(pGlfwRequiredExtensions);
                if (usePortability) {
                    pRequiredExtensions.put(stack.UTF8(KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME));
                }
            }
            pRequiredExtensions.flip();

            long extension = MemoryUtil.NULL;
            if (supportsValidation) {
                debugUtils = createDebugCallBack();
                extension = debugUtils.address();
            }

            //Instance

            VkInstanceCreateInfo instanceInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(extension)
                    .pApplicationInfo(appInfo)
                    .ppEnabledLayerNames(pRequiredLayers)
                    .ppEnabledExtensionNames(pRequiredExtensions);
            if (usePortability) {
                instanceInfo.flags(0x00000001); // VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR
            }

            PointerBuffer pInstance = stack.mallocPointer(1);
            VulkanUtils.vkCheck(vkCreateInstance(instanceInfo, null, pInstance), "Error creating instance");
            vkInstance = new VkInstance(pInstance.get(0), instanceInfo);

            vkDebugHandle = VK_NULL_HANDLE;
            if (supportsValidation) {
                LongBuffer longBuff = stack.mallocLong(1);
                VulkanUtils.vkCheck(vkCreateDebugUtilsMessengerEXT(vkInstance, debugUtils, null, longBuff), "Error creating debug utils");
                vkDebugHandle = longBuff.get(0);
            }
        }
    }

    public void cleanup() {
        if (vkDebugHandle != VK_NULL_HANDLE) {
            vkDestroyDebugUtilsMessengerEXT(vkInstance, vkDebugHandle, null);
        }
        if (debugUtils != null) {
            debugUtils.pfnUserCallback().free();
            debugUtils.free();
        }
        vkDestroyInstance(vkInstance, null);
    }

    public VkInstance getVkInstance() {
        return vkInstance;
    }

    private List<String> getSupportedValidationLayers(MemoryStack stack) {
        IntBuffer pNumLayers = stack.callocInt(1);
        vkEnumerateInstanceLayerProperties(pNumLayers, null);
        int numLayers = pNumLayers.get(0);
        VkLayerProperties.Buffer layerProperties = VkLayerProperties.calloc(numLayers, stack);
        vkEnumerateInstanceLayerProperties(pNumLayers, layerProperties);
        List<String> layers = new ArrayList<>();
        for (int i = 0; i < numLayers; i++) {
            VkLayerProperties layer = layerProperties.get(i);
            String layerName = layer.layerNameString();
            layers.add(layerName);
        }
        List<String> layersToUse = new ArrayList<>();

        // Main validation layer
        if (layers.contains("VK_LAYER_KHRONOS_validation")) {
            layersToUse.add("VK_LAYER_KHRONOS_validation");
            return layersToUse;
        }

        // Fallback 1
        if (layers.contains("VK_LAYER_LUNARG_standard_validation")) {
            layersToUse.add("VK_LAYER_LUNARG_standard_validation");
            return layersToUse;
        }

        // Fallback 2 (set)
        List<String> requestedLayers = new ArrayList<>();
        requestedLayers.add("VK_LAYER_GOOGLE_threading");
        requestedLayers.add("VK_LAYER_LUNARG_parameter_validation");
        requestedLayers.add("VK_LAYER_LUNARG_object_tracker");
        requestedLayers.add("VK_LAYER_LUNARG_core_validation");
        requestedLayers.add("VK_LAYER_GOOGLE_unique_objects");

        return requestedLayers.stream().filter(layers::contains).toList();
    }

    private Set<String> getInstanceExtensions(MemoryStack stack) {
        Set<String> instanceExtensions = new HashSet<>();
        IntBuffer numExtensionsBuf = stack.callocInt(1);
        vkEnumerateInstanceExtensionProperties((String) null, numExtensionsBuf, null);
        int numExtensions = numExtensionsBuf.get(0);

        VkExtensionProperties.Buffer instanceExtensionsProps = VkExtensionProperties.calloc(numExtensions, stack);
        vkEnumerateInstanceExtensionProperties((String) null, numExtensionsBuf, instanceExtensionsProps);
        for (int i = 0; i < numExtensions; i++) {
            VkExtensionProperties props = instanceExtensionsProps.get(i);
            String extensionName = props.extensionNameString();
            instanceExtensions.add(extensionName);
        }
        return instanceExtensions;
    }

    private VkDebugUtilsMessengerCreateInfoEXT createDebugCallBack() {
        return VkDebugUtilsMessengerCreateInfoEXT
                .calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
                .messageSeverity(MESSAGE_SEVERITY_BITMASK)
                .messageType(MESSAGE_TYPE_BITMASK)
                .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                    VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                    if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                        Oxygen.LOGGER.info("VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                        Oxygen.LOGGER.warn("VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                        Oxygen.LOGGER.warn("VkDebugUtilsCallback, {}", callbackData.pMessageString());
                    }
                    return VK_FALSE;
                });
    }

}
