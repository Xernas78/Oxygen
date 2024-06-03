package dev.xernas.oxygen;

import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.exception.GLFWException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.logging.OLogger;
import dev.xernas.oxygen.render.Renderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Oxygen {

    private static final long NANOSECOND = 1000000000L;
    private static final float FRAMERATE = 1000f;
    private static final float FRAMETIME = 1.0f / FRAMERATE;

    public static final OLogger LOGGER = new OLogger();

    private final String applicationName;
    private final String version;
    private final boolean vsync;
    private final boolean debug;

    private final Window window;
    private final Renderer renderer;

    private final List<Scene> scenes;

    private boolean running = false;
    private int fps;
    private int currentSceneIndex = 0;

    public Oxygen(String applicationName, String version, Window window, boolean vsync, boolean debug) {
        this.applicationName = applicationName;
        this.version = version;
        this.vsync = vsync;
        this.debug = debug;
        this.window = window;
        this.renderer = new Renderer(this);
        this.scenes = new ArrayList<>();
    }


    public void init() throws OxygenException {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        try {
            if (!glfwInit()) throw new GLFWException("Unable to initialize GLFW");
            //TODO OpenGL
            //if (!glfwVulkanSupported()) throw new GLFWException("Cannot find a compatible Vulkan installable client driver (ICD)");
            window.init();
            renderer.init();

            window.show();

            if (scenes.isEmpty()) throw new OxygenException("Can't find any scene");
            scenes.get(currentSceneIndex).startObjects(this);

            //TODO OpenGL init

            loop();
            cleanup();
        } catch (OxygenException e) {
            LOGGER.fatal(e);
        }
    }

    private void loop() throws OxygenException {
        running = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        float unprocessedTime = 0;

        while (running) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (float) NANOSECOND;
            frameCounter += passedTime;

            //TODO SwapBuffers
            window.pollEvents();

            scenes.get(currentSceneIndex).inputObjects(this);

            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;
                if (window.shouldClose()) stop();
                if (frameCounter >= NANOSECOND) {
                    scenes.get(currentSceneIndex).updateObjects(this);
                    setFps(frames);
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                renderer.render();
                frames++;
            }
        }
    }

    private void cleanup() throws OxygenException {
        scenes.get(currentSceneIndex).cleanupObjects();
        renderer.cleanup();
        window.cleanup();
        glfwTerminate();
    }

    public void addScene(Scene scene) {
        scenes.add(scene);
    }

    public void setScene(int index) throws OxygenException {
        if (index < 0 || index >= scenes.size()) {
            LOGGER.warn("Scene index out of bounds");
            return;
        }
        scenes.get(currentSceneIndex).cleanupObjects();
        currentSceneIndex = index;
        scenes.get(currentSceneIndex).startObjects(this);
    }

    public void stop() {
        running = false;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean debugEnabled() {
        return debug;
    }

    public String getVersion() {
        return version;
    }

    public Window getWindow() {
        return window;
    }
    public Renderer getRenderer() {
        return renderer;
    }

    public boolean isVsync() {
        return vsync;
    }

    public static class Builder {

        private String applicationName = "Oxygen";
        private String version = "1.0.0";
        private String title = "Oxygen Graphics";
        private Integer width = 1280;
        private Integer height = 720;
        private Boolean resizable = false;
        private Boolean maximized = false;
        private Boolean vsync = false;
        private Boolean debug = false;

        public Builder applicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder resizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }

        public Builder maximized(boolean maximized) {
            this.maximized = maximized;
            return this;
        }

        public Builder vsync(boolean vsync) {
            this.vsync = vsync;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Oxygen build() {
            return new Oxygen(applicationName, version, new Window(title, width, height, resizable, maximized), vsync, debug);
        }
    }
}
