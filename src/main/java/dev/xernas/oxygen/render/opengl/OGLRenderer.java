package dev.xernas.oxygen.render.opengl;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.LightSource;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModel;
import dev.xernas.oxygen.engine.resource.ResourceManager;

import java.io.File;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class OGLRenderer implements IRenderer {

    private final List<SceneObject> sceneObjects = new ArrayList<>();
    private final Map<String, OGLShaderProgram> shaderPrograms = new HashMap<>();

    private String currentShaderProgramKey;

    private final Window window;

    public OGLRenderer(Oxygen oxygen) {
        this.window = oxygen.getWindow();
    }

    @Override
    public void render() throws OxygenException {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(window.getClearColor().getRed() / 255f, window.getClearColor().getGreen() / 255f, window.getClearColor().getBlue() / 255f, window.getClearColor().getAlpha() / 255f);
        LightSource.lightIndex = 0;
        for (SceneObject sceneObject : sceneObjects) {
            currentShaderProgramKey = sceneObject.getShaderName();
            getCurrentShaderProgram().bind();
            sceneObject.renderBehaviors(this);
            getCurrentShaderProgram().unbind();
        }
    }

    @Override
    public void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException {
        this.sceneObjects.addAll(sceneObjects);
    }

    @Override
    public void loadSceneObject(SceneObject sceneObject) throws OxygenException {
        this.sceneObjects.add(sceneObject);
    }

    public void loadShaderPrograms(List<OGLShaderProgram> shaderPrograms) {
        for (OGLShaderProgram shaderProgram : shaderPrograms) {
            this.shaderPrograms.put(shaderProgram.getShaderName(), shaderProgram);
        }
    }

    @Override
    public void init() throws OxygenException {
        loadShaderPrograms(Oxygen.OXYGEN_RESOURCE_MANAGER.getShadersFromShadersDir());
        loadShaderPrograms(Oxygen.getRemoteResourceManager().getShadersFromShadersDir());
        for (OGLShaderProgram shaderProgram : shaderPrograms.values()) shaderProgram.init();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void cleanup() throws OxygenException {
        for (OGLShaderProgram shaderProgram : shaderPrograms.values()) shaderProgram.cleanup();
    }

    public OGLShaderProgram getShaderProgram(String name) {
        return shaderPrograms.get(name);
    }

    public OGLShaderProgram getCurrentShaderProgram() throws OpenGLException {
        OGLShaderProgram shaderProgram = getShaderProgram(currentShaderProgramKey);
        if (shaderProgram == null) throw new OpenGLException("Shader program not found: " + currentShaderProgramKey);
        return shaderProgram;
    }

    public Window getWindow() {
        return window;
    }
}
