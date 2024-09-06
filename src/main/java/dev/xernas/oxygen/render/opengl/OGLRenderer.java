package dev.xernas.oxygen.render.opengl;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.SceneEntity;
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
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

public class OGLRenderer implements IRenderer {

    private static final Map<String, Map<Integer, List<SceneEntity>>> batches = new HashMap<>();

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
            Map<Integer, List<SceneEntity>> modelBatches = OGLRenderer.batches.get(shaderName);
            Oxygen.LOGGER.debug("------------------------------------", true);
            Oxygen.LOGGER.debug("- Rendering shader program: " + shaderName, true);
            currentShaderProgramKey = shaderName;
            getCurrentShaderProgram().bind();
            getCurrentShaderProgram().setUniform("projectionMatrix", TransformUtils.createProjectionMatrix(window));
            getCurrentShaderProgram().setUniform("orthoMatrix", TransformUtils.createOrthoMatrix(window));

            for (Integer modelData : modelBatches.keySet()) {
                List<SceneEntity> sceneEntities = modelBatches.get(modelData);

                Oxygen.LOGGER.debug("   - Rendering " + sceneEntities.size() + " models data with id : " + modelData, true);

                if (modelData != -1) bindModel(OGLModel.byId(modelData).getModelData());
                firstOfBatch = true;
                for (SceneEntity sceneEntity : sceneEntities) {
                    sceneEntity.renderBehaviors(this);
                    firstOfBatch = false;
                }
                if (modelData != -1) unbindModel(OGLModel.byId(modelData).getModelData());
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

    public static void addSceneObjectToBatch(SceneEntity sceneEntity, Integer modelData) {
        Map<Integer, List<SceneEntity>> modelBatches = batches.get(sceneEntity.getShaderName());
        if (modelBatches == null) modelBatches = new HashMap<>();
        List<SceneEntity> modelBatch = modelBatches.get(modelData);
        if (modelBatch == null) modelBatch = new ArrayList<>();
        modelBatch.add(sceneEntity);
        modelBatches.put(modelData, modelBatch);
        batches.put(sceneEntity.getShaderName(), modelBatches);
    }

    @Override
    public void loadSceneObjects(List<SceneEntity> sceneEntities) {
        for (SceneEntity sceneEntity : sceneEntities) loadSceneObject(sceneEntity);
    }

    @Override
    public void loadSceneObject(SceneEntity sceneEntity) {
        ModelRenderer modelRenderer = sceneEntity.getBehavior(ModelRenderer.class);
        int modelDataId = -1;
        if (modelRenderer != null) modelDataId = ((OGLModelData) modelRenderer.getModelData()).getId();
        addSceneObjectToBatch(sceneEntity, modelDataId);
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
        enableDepthTest();
        enableBackfaceCulling();
    }

    public void enableBackfaceCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void disableBackfaceCulling() {
        glDisable(GL_CULL_FACE);
    }

    public void enableDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }

    public void disableDepthTest() {
        glDisable(GL_DEPTH_TEST);
    }

    public void clear() {
        batches.clear();
    }

    @Override
    public void cleanup() throws OxygenException {
        clear();
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
