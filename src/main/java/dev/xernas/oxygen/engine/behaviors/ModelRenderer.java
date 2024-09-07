package dev.xernas.oxygen.engine.behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.material.TexturedMaterial;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.opengl.model.OGLModel;
import dev.xernas.oxygen.render.opengl.model.OGLModelData;
import dev.xernas.oxygen.render.model.IModelData;
import dev.xernas.oxygen.engine.model.Model;
import dev.xernas.oxygen.render.utils.Lib;
import dev.xernas.oxygen.render.vulkan.model.VulkanModelData;

import java.util.*;

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
    public final void awake(Oxygen oxygen, SceneEntity parent) throws OxygenException {
        if (Oxygen.getLib() == Lib.OPENGL) {
            modelData = new OGLModelData(model.getVertices(), model.getIndices(), model.getNormals(), model.getTextureCoords(), model.getMaterial() instanceof TexturedMaterial ? ((TexturedMaterial)model.getMaterial()).getTexturePath(true) : null);
            oglModel = OGLModel.transformModel(modelData);
        } else if (Oxygen.getLib() == Lib.VULKAN) {
            modelData = new VulkanModelData(Oxygen.getVulkanModelIdCounter(), Collections.singletonList(new VulkanModelData.MeshData(model.getVertices(), model.getIndices())));
            Oxygen.incrementVulkanModelIdCounter();
        }
    }

    @Override
    public void start(Oxygen oxygen, SceneEntity parent) throws OxygenException {

    }

    @Override
    public void update(Oxygen oxygen, SceneEntity parent) {

    }

    @Override
    public final void render(OGLRenderer renderer, SceneEntity parent) throws OxygenException {
        if (modelData == null) return;
        OGLModelData currentModelData = oglModel.getModelData();

        renderer.getCurrentShaderProgram().setUniform("visible", parent.isVisible());
        renderer.getCurrentShaderProgram().setUniform("textureSampler", 0);

        boolean textured = false;
        if (model.getMaterial() instanceof TexturedMaterial texturedMaterial) {
            renderer.getCurrentShaderProgram().setUniform("numTextureTiles", texturedMaterial.getTextureTiles());
            textured = true;
        }
        renderer.getCurrentShaderProgram().setUniform("isTextured", textured);
        renderer.getCurrentShaderProgram().setUniform("illuminable", model.getMaterial().illuminable());
        renderer.getCurrentShaderProgram().setUniform("reflectionVisibility", model.getMaterial().getReflectionVisibility());
        renderer.getCurrentShaderProgram().setUniform("reflectivity", model.getMaterial().getReflectivity());
        renderer.getCurrentShaderProgram().setUniform("baseColor", model.getMaterial().getBaseColor());

        if (model.getMaterial().backfaceCullingDisabled()) renderer.disableBackfaceCulling();

        if (currentModelData.is2D()) renderer.disableDepthTest();

        renderer.drawElements(currentModelData);

        if (currentModelData.is2D()) renderer.enableDepthTest();

        if (model.getMaterial().backfaceCullingDisabled()) renderer.enableBackfaceCulling();
    }

    @Override
    public final void cleanup(Oxygen oxygen, SceneEntity parent) throws OxygenException {
        oglModel.cleanup();
    }

    public final Model getModel() {
        return model;
    }

    public final IModelData getModelData() {
        return modelData;
    }
}
