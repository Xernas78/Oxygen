package dev.xernas.oxygen.render.opengl;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.LightSource;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;
import dev.xernas.oxygen.render.opengl.model.OGLModel;
import dev.xernas.oxygen.render.opengl.model.OGLModelData;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;
import dev.xernas.oxygen.render.utils.TransformUtils;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class OGLRenderer implements IRenderer {

    private static final Map<String, Map<Integer, List<SceneObject>>> batches = new HashMap<>();

    private final List<SceneObject> noModelSceneObjects = new ArrayList<>();
    private final Map<String, OGLShaderProgram> shaderPrograms = new HashMap<>();

    private String currentShaderProgramKey;
    private boolean firstOfBatch = false;

    private final Window window;

    public OGLRenderer(Oxygen oxygen) {
        this.window = oxygen.getWindow();
    }

    @Override
    public void render() throws OxygenException {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(window.getClearColor().getRed() / 255f, window.getClearColor().getGreen() / 255f, window.getClearColor().getBlue() / 255f, window.getClearColor().getAlpha() / 255f);
        LightSource.lightIndex = 0;
        ModelRenderer.bindsPerFrame = 0;
        ModelRenderer.unbindsPerFrame = 0;
        for (String shaderName : batches.keySet()) {
            Map<Integer, List<SceneObject>> modelBatches = OGLRenderer.batches.get(shaderName);
            currentShaderProgramKey = shaderName;
            getCurrentShaderProgram().bind();
            getCurrentShaderProgram().setUniform("projectionMatrix", TransformUtils.createProjectionMatrix(window));

            for (SceneObject sceneObject : noModelSceneObjects) {
                sceneObject.renderBehaviors(this);
            }

            for (Integer modelData : modelBatches.keySet()) {
                List<SceneObject> sceneObjects = modelBatches.get(modelData);
                bindModel(OGLModel.byId(modelData).getModelData());
                firstOfBatch = true;
                for (SceneObject sceneObject : sceneObjects) {
                    sceneObject.renderBehaviors(this);
                    firstOfBatch = false;
                }
                unbindModel(OGLModel.byId(modelData).getModelData());
            }
            getCurrentShaderProgram().unbind();
        }
    }

    public void bindModel(OGLModelData modelData) throws OpenGLException {
        modelData.bind();
        glEnableVertexAttribArray(0);
        if (modelData.hasTexture()) glEnableVertexAttribArray(1);
        if (modelData.hasNormals()) glEnableVertexAttribArray(2);
    }

    public void unbindModel(OGLModelData modelData) throws OpenGLException {
        glDisableVertexAttribArray(0);
        if (modelData.hasTexture()) glDisableVertexAttribArray(1);
        if (modelData.hasNormals()) glDisableVertexAttribArray(2);
        modelData.unbind();
    }

    public void drawElements(OGLModelData currentModelData) {
        glDrawElements(GL_TRIANGLES, currentModelData.getIndicesCount(), GL_UNSIGNED_INT, 0);
    }

    public static void addSceneObjectToBatch(SceneObject sceneObject, Integer modelData) {
        Map<Integer, List<SceneObject>> modelBatches = batches.get(sceneObject.getShaderName());
        if (modelBatches == null) modelBatches = new HashMap<>();
        List<SceneObject> modelBatch = modelBatches.get(modelData);
        if (modelBatch == null) modelBatch = new ArrayList<>();
        modelBatch.add(sceneObject);
        modelBatches.put(modelData, modelBatch);
        batches.put(sceneObject.getShaderName(), modelBatches);
    }

    @Override
    public void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException {
        for (SceneObject sceneObject : sceneObjects) loadSceneObject(sceneObject);
    }

    @Override
    public void loadSceneObject(SceneObject sceneObject) throws OxygenException {
        ModelRenderer modelRenderer = sceneObject.getBehavior(ModelRenderer.class);
        if (modelRenderer == null) this.noModelSceneObjects.add(sceneObject);
        else {
            OGLModelData oglModelData = (OGLModelData) modelRenderer.getModelData();
            addSceneObjectToBatch(sceneObject, oglModelData.getId());
        }
    }

    public void loadShaderPrograms(List<OGLShaderProgram> shaderPrograms) {
        for (OGLShaderProgram shaderProgram : shaderPrograms) {
            this.shaderPrograms.put(shaderProgram.getShaderName(), shaderProgram);
        }
    }

    public boolean isFirstOfBatch() {
        return firstOfBatch;
    }

    @Override
    public void init() throws OxygenException {
        loadShaderPrograms(Oxygen.OXYGEN_RESOURCE_MANAGER.getShadersFromShadersDir());
        loadShaderPrograms(Oxygen.getRemoteResourceManager().getShadersFromShadersDir());
        for (OGLShaderProgram shaderProgram : shaderPrograms.values()) shaderProgram.init();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
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
