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

    private final Model model;

    public static int bindsPerFrame = 0;
    public static int unbindsPerFrame = 0;

    private IModelData modelData;

    private OGLModel oglModel;

    public ModelRenderer(Model model) {
        this.model = model;
    }

    @Override
    public final void awake(Oxygen oxygen, SceneObject parent) throws OxygenException {
        if (Oxygen.getLib() == Lib.OPENGL) {
            modelData = new OGLModelData(model.getVertices(), model.getIndices(), model.getNormals(), model.getTextureCoords(), model.getMaterial().getTexturePath());
            oglModel = OGLModel.transformModel(modelData);
        } else if (Oxygen.getLib() == Lib.VULKAN) {
            modelData = new VulkanModelData(Oxygen.getVulkanModelIdCounter(), Collections.singletonList(new VulkanModelData.MeshData(model.getVertices(), model.getIndices())));
            Oxygen.incrementVulkanModelIdCounter();
        }
    }

    @Override
    public void start(Oxygen oxygen, SceneObject parent) throws OxygenException {

    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {

    }

    @Override
    public final void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        if (modelData == null) return;
        OGLModelData currentModelData = oglModel.getModelData();

        if (renderer.isFirstOfBatch()) {
            renderer.getCurrentShaderProgram().setUniform("textureSampler", 0);
            renderer.getCurrentShaderProgram().setUniform("isTextured", currentModelData.hasTexture());
            renderer.getCurrentShaderProgram().setUniform("numTextureTiles", model.getMaterial().getTextureTiles());
            renderer.getCurrentShaderProgram().setUniform("illuminable", model.getMaterial().illuminable());
            renderer.getCurrentShaderProgram().setUniform("reflectionVisibility", model.getMaterial().getReflectionVisibility());
            renderer.getCurrentShaderProgram().setUniform("reflectivity", model.getMaterial().getReflectivity());
            renderer.getCurrentShaderProgram().setUniform("baseColor", model.getMaterial().getBaseColor());
        }

        renderer.drawElements(currentModelData);
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
