package dev.xernas.oxygen;

import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.exception.GLFWException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.logging.OLogger;
import dev.xernas.oxygen.render.IRenderer;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.utils.EmptyRenderer;
import dev.xernas.oxygen.render.utils.Lib;
import dev.xernas.oxygen.render.vulkan.VulkanRenderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

public class Oxygen {

    private static final long SECOND = 1000000000L;
    private static final long MILLISSECOND = 1000000L;
    private static final float TARGET_FRAMERATE = 1000f;
    private static final float FRAMETIME = 1.0f / TARGET_FRAMERATE;

    public static final OLogger LOGGER = new OLogger();
    public static final ResourceManager OXYGEN_RESOURCE_MANAGER = new ResourceManager(Oxygen.class);

    private final String applicationName;
    private final String version;
    private final boolean vsync;
    private final boolean debug;

    private final Window window;
    private final IRenderer renderer;

    private static final List<Scene> scenes = new ArrayList<>();

    private static boolean running = false;
    private static int fps;
    private static int deltaTime;
    private static Lib lib;
    private static int currentSceneIndex = 0;

    private static int vulkanModelIdCounter = 0;

    public Oxygen(String applicationName, String version, Window window, boolean vsync, boolean debug, Lib lib) {
        this.applicationName = applicationName;
        this.version = version;
        this.vsync = vsync;
        this.debug = debug;
        Oxygen.lib = lib;
        this.window = window;
        if (lib == Lib.VULKAN) this.renderer = new VulkanRenderer(this);
        else if (lib == Lib.OPENGL) this.renderer = new OGLRenderer(this);
        else {
            LOGGER.warn("Unsupported library, using empty renderer");
            this.renderer = new EmptyRenderer();
        }
    }


    public void init() {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        try {
            if (!glfwInit()) throw new GLFWException("Unable to initialize GLFW");

            if (lib == Lib.VULKAN) if (!glfwVulkanSupported()) throw new GLFWException("Cannot find a compatible Vulkan installable client driver (ICD)");

            window.init();
            renderer.init();

            if (debug) if (lib == Lib.OPENGL) GLUtil.setupDebugMessageCallback();

            if (scenes.isEmpty()) throw new OxygenException("Can't find any scene");
            scenes.get(currentSceneIndex).startObjects(this);

            window.show();

            loop();
            window.hide();
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

            setDeltaTime((int) (passedTime / MILLISSECOND));
            unprocessedTime += passedTime / (float) SECOND;
            frameCounter += passedTime;

            window.update();

            scenes.get(currentSceneIndex).inputObjects(this);

            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;
                if (window.shouldClose()) {
                    stop();
                    return;
                }
                scenes.get(currentSceneIndex).updateObjects(this);
                if (frameCounter >= SECOND) {
                    // Executes every second
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
        getCurrentScene().cleanupObjects(this);
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
        getCurrentScene().cleanupObjects(this);
        currentSceneIndex = index;
        getCurrentScene().startObjects(this);
    }

    public static Scene getScene(int index) {
        return scenes.get(index);
    }

    public static Scene getCurrentScene() {
        return getScene(currentSceneIndex);
    }

    public static <T> T getFirstObject(Class<? extends SceneObject> objectClass) {
        return getCurrentScene().getFirstObject(objectClass);
    }

    public static <T> List<T> getObjects(Class<? extends SceneObject> objectClass) {
        return getCurrentScene().getObjects(objectClass);
    }

    public void stop() {
        running = false;
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        Oxygen.fps = fps;
    }

    public static int getDeltaTime() {
        return deltaTime;
    }

    public static void setDeltaTime(int deltaTime) {
        Oxygen.deltaTime = deltaTime;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean debugEnabled() {
        return debug;
    }

    public static Lib getLib() {
        return lib;
    }

    public static int getVulkanModelIdCounter() {
        return vulkanModelIdCounter;
    }

    public static void incrementVulkanModelIdCounter() {
        vulkanModelIdCounter++;
    }

    public String getVersion() {
        return version;
    }

    public Window getWindow() {
        return window;
    }
    public IRenderer getRenderer() {
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
        private Lib lib = Lib.OPENGL;
        private String absoluteIconPath = OXYGEN_RESOURCE_MANAGER.getFileResourceAbsolutePath("textures/oxygen.png");

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

        public Builder lib(Lib lib) {
            this.lib = lib;
            return this;
        }

        public Builder absoluteIconPath(String absoluteIconPath) {
            this.absoluteIconPath = absoluteIconPath;
            return this;
        }

        public Oxygen build() {
            return new Oxygen(applicationName, version, new Window(title, width, height, resizable, maximized, vsync, absoluteIconPath), vsync, debug, lib);
        }
    }
}
