package dev.xernas.oxygen;

import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.engine.input.Key;
import dev.xernas.oxygen.engine.resource.img.Image;
import dev.xernas.oxygen.exception.GLFWException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.utils.OGLUtils;
import dev.xernas.oxygen.render.utils.Lib;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements IOxygenLogic {

    private final String defaultTitle;

    private long windowHandle;

    private String title;
    private int width;
    private int height;
    private final boolean resizable;
    private boolean maximized;
    private final boolean vsync;
    private final String absoluteIconPath;

    private final Input input;

    private Color clearColor = Color.WHITE;

    public Window(String title, int width, int height, boolean resizable, boolean maximized, boolean vsync, String absoluteIconPath) {
        this.defaultTitle = title;
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizable = resizable;
        this.maximized = maximized;
        this.vsync = vsync;
        this.input = new Input(this);
        this.absoluteIconPath = absoluteIconPath;
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

        if (Oxygen.getLib() == Lib.OPENGL) {
            glfwMakeContextCurrent(windowHandle);
            if (vsync) glfwSwapInterval(1);
            else glfwSwapInterval(0);
            GL.createCapabilities();
        }

        Image icon = OGLUtils.loadImage(absoluteIconPath);
        ByteBuffer iconBuffer = icon.getData();
        try (GLFWImage.Buffer icons = GLFWImage.create(1)) {
            GLFWImage iconImage = GLFWImage.create().set(icon.getWidth(), icon.getHeight(), iconBuffer);
            icons.put(0, iconImage);
            glfwSetWindowIcon(windowHandle, icons);
        }
    }

    @Override
    public void cleanup() throws OxygenException {
        close();
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    private void configure() {
        glfwDefaultWindowHints();
        if (Oxygen.getLib() == Lib.VULKAN) glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        if (resizable) glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        if (!vsync) glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);
        glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        if (Oxygen.getLib() == Lib.OPENGL) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }
    }

    private void callbacks() {
        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resize(w, h));

//        // Keyboard
//        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> input.getOnKey().accept(new KeyAction(key, action)));
//
//        // Mouse
//        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> input.getOnMouseButton().accept(new KeyAction(button, action)));
//        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> input.getOnScroll().accept(yoffset));
//
//        // Cursor
//        glfwSetCursorPosCallback(windowHandle, (window, x, y) -> input.getCursor().update(x, y));
//        glfwSetCursorEnterCallback(windowHandle, (window, entered) -> input.getOnCursorEnter().accept(input));
    }

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;
        if (Oxygen.getLib() == Lib.OPENGL) glViewport(0, 0, width, height);
        glfwSetWindowSize(windowHandle, width, height);
    }

    public void update() {
        glfwPollEvents();
        if (Oxygen.getLib() == Lib.OPENGL) glfwSwapBuffers(windowHandle);
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

    public boolean isMinimized() {
        return glfwGetWindowAttrib(windowHandle, GLFW_ICONIFIED) == GLFW_TRUE;
    }

    public void minimize() {
        glfwIconifyWindow(windowHandle);
    }

    public void restore() {
        glfwRestoreWindow(windowHandle);
    }

    public void close() {
        glfwSetWindowShouldClose(windowHandle, true);
    }

    public void setBackgroundColor(Color color) {
        this.clearColor = color;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    public boolean isKeyPressed(Key key) {
        return glfwGetKey(windowHandle, key.getQwerty()) == GLFW_PRESS;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public Input getInput() {
        return input;
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