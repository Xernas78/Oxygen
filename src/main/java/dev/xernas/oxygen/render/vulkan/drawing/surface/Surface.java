package dev.xernas.oxygen.render.vulkan.drawing.surface;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.Instance;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;

import java.nio.LongBuffer;

public class Surface implements IOxygenLogic {

    private final Instance instance;
    private final Window window;
    private long vkSurface;

    public Surface(Instance instance, Window window) {
        this.instance = instance;
        this.window = window;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.mallocLong(1);
            GLFWVulkan.glfwCreateWindowSurface(instance.getVkInstance(), window.getWindowHandle(),
                    null, pSurface);
            vkSurface = pSurface.get(0);
        }
    }

    public void cleanup() {
        KHRSurface.vkDestroySurfaceKHR(instance.getVkInstance(), vkSurface, null);
    }

    public long getVkSurface() {
        return vkSurface;
    }

}
