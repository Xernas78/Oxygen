package dev.xernas.oxygen;

import dev.xernas.oxygen.exception.GLFWException;
import dev.xernas.oxygen.exception.OxygenException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements IOxygenLogic {

    private final String defaultTitle;

    private long windowHandle;

    private String title;
    private int width;
    private int height;
    private final boolean resizable;
    private boolean maximized;
    private Color clearColor = Color.WHITE;

    public Window(String title, int width, int height, boolean resizable, boolean maximized) {
        this.defaultTitle = title;
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizable = resizable;
        this.maximized = maximized;
    }

    @Override
    public void init() throws OxygenException {
        configure();
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) throw new GLFWException("Failed to create the GLFW window");
        callbacks();

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode == null) throw new GLFWException("Failed to get video mode");
        glfwSetWindowPos(
                windowHandle,
                (videoMode.width() - width) / 2,
                (videoMode.height() - height) / 2
        );
        if (maximized) maximize();
    }

    @Override
    public void cleanup() throws OxygenException {
        close();
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    private void configure() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        if (resizable) glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
    }

    private void callbacks() {
        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resize(w, h));
    }

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;
        glfwSetWindowSize(windowHandle, width, height);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void show() {
        glfwShowWindow(windowHandle);
    }

    public void hide() {
        glfwHideWindow(windowHandle);
    }

    public void maximize() {
        glfwMaximizeWindow(windowHandle);
    }

    public void close() {
        glfwSetWindowShouldClose(windowHandle, true);
    }

    public void setClearColor(Color color) {
        this.clearColor = color;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public Color getClearColor() {
        return clearColor;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
