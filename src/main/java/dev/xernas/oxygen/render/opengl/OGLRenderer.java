package dev.xernas.oxygen.render.opengl;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModel;
import dev.xernas.oxygen.engine.resource.ResourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class OGLRenderer implements IRenderer {

    private final List<SceneObject> sceneObjects = new ArrayList<>();
    private final List<IModel> models = new ArrayList<>();

    private OGLShaderProgram shaderProgram;

    private final Window window;

    public OGLRenderer(Oxygen oxygen) {
        this.window = oxygen.getWindow();
    }

    @Override
    public void render() throws OxygenException {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(window.getClearColor().getRed() / 255f, window.getClearColor().getGreen() / 255f, window.getClearColor().getBlue() / 255f, window.getClearColor().getAlpha() / 255f);
        shaderProgram.bind();
        for (SceneObject sceneObject : sceneObjects) {
            sceneObject.renderBehaviors(this);
        }
        shaderProgram.unbind();
    }

    @Override
    public void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException {
        this.sceneObjects.addAll(sceneObjects);
    }

    @Override
    public void loadSceneObject(SceneObject sceneObject) throws OxygenException {
        this.sceneObjects.add(sceneObject);
    }

    @Override
    public void init() throws OxygenException {
        File shaderDir = new File("shaders/");
        if (!shaderDir.exists()) {
            if (!shaderDir.mkdir()) throw new OpenGLException("Error creating shaders directory");
        }
        File defaultShaderDir = new File("shaders/default/");
        if (!defaultShaderDir.exists()) {
            if (!defaultShaderDir.mkdir()) throw new OxygenException("Error creating default shader directory");
            Oxygen.OXYGEN_RESOURCE_MANAGER.createFileFromResource("shaders/default/default.vert", "shaders/default/default.vert");
            Oxygen.OXYGEN_RESOURCE_MANAGER.createFileFromResource("shaders/default/default.frag", "shaders/default/default.frag");
        }
        shaderProgram = OGLShaderProgram.DEFAULT;
        shaderProgram.init();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void cleanup() throws OxygenException {
        shaderProgram.cleanup();
    }

    public OGLShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public Window getWindow() {
        return window;
    }

    public void addModel(IModel model) {
        models.add(model);
    }
}
