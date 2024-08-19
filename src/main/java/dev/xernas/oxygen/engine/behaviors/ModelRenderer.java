package dev.xernas.oxygen.engine.behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.opengl.model.OGLModel;
import dev.xernas.oxygen.render.opengl.model.OGLModelData;
import dev.xernas.oxygen.render.model.IModelData;
import dev.xernas.oxygen.engine.model.Model;
import dev.xernas.oxygen.render.utils.Lib;
import dev.xernas.oxygen.render.vulkan.model.VulkanModelData;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class ModelRenderer implements Behavior {

    private static final Map<Integer, List<OGLModelData>> batches = new HashMap<>();
    private final Model model;

    private IModelData modelData;

    private OGLModel oglModel;

    public ModelRenderer(Model model) {
        this.model = model;
    }

    @Override
    public final void start(Oxygen oxygen, SceneObject parent) throws OxygenException {
        if (Oxygen.getLib() == Lib.OPENGL) {
            modelData = new OGLModelData(model.getVertices(), model.getIndices(), model.getNormals(), model.getTextureCoords(), model.getMaterial().getTexturePath());
            oglModel = OGLModel.transformModel(modelData);
//            List<OGLModelData> batch = batches.getOrDefault(oglModel.getModelId(), new ArrayList<>());
//            batch.add(oglModel.getModelData());
//            batches.put(oglModel.getModelId(), batch);
//            Oxygen.LOGGER.debug("---------");
//            Oxygen.LOGGER.debug("Current batches size: " + batches.size());
//            int i = 0;
//            for (List<OGLModelData> oglModelData : batches.values()) {
//                Oxygen.LOGGER.debugList(oglModelData, "Batch n°" + i + " : {}");
//                i++;
//            }
        } else if (Oxygen.getLib() == Lib.VULKAN) {
            modelData = new VulkanModelData(Oxygen.getVulkanModelIdCounter(), Collections.singletonList(new VulkanModelData.MeshData(model.getVertices(), model.getIndices())));
            Oxygen.incrementVulkanModelIdCounter();
        }
    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {

    }

    @Override
    public final void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        if (modelData == null) return;
        OGLModelData modelData = oglModel.getModelData();

        renderer.getCurrentShaderProgram().setUniform("textureSampler", 0);
        renderer.getCurrentShaderProgram().setUniform("isTextured", modelData.hasTexture());
        renderer.getCurrentShaderProgram().setUniform("illuminable", model.getMaterial().illuminable());
        renderer.getCurrentShaderProgram().setUniform("reflectionVisibility", model.getMaterial().getReflectionVisibility());
        renderer.getCurrentShaderProgram().setUniform("reflectivity", model.getMaterial().getReflectivity());
        renderer.getCurrentShaderProgram().setUniform("baseColor", model.getMaterial().getBaseColor());

        modelData.bind();
        glEnableVertexAttribArray(0);
        if (modelData.hasTexture()) glEnableVertexAttribArray(1);
        if (modelData.hasNormals()) glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, modelData.getIndicesCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        if (modelData.hasTexture()) glDisableVertexAttribArray(1);
        if (modelData.hasNormals()) glDisableVertexAttribArray(2);
        modelData.unbind();
    }

    @Override
    public final void cleanup(Oxygen oxygen, SceneObject parent) throws OxygenException {
        oglModel.cleanup();
    }

    public final Model getModel() {
        return model;
    }

    public final IModelData getModelData() {
        return modelData;
    }
}
