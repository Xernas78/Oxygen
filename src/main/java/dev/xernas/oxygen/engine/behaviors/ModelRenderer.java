package dev.xernas.oxygen.engine.behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.opengl.model.OGLModel;
import dev.xernas.oxygen.render.opengl.model.OGLModelData;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModelData;
import dev.xernas.oxygen.render.oxygen.model.Model;
import dev.xernas.oxygen.render.utils.Lib;
import dev.xernas.oxygen.render.vulkan.model.VulkanModelData;

import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class ModelRenderer implements Behavior {

    private final Model model;

    private IModelData modelData;

    public ModelRenderer(Model model) {
        this.model = model;
    }



    @Override
    public void start(Oxygen oxygen, SceneObject parent) throws OxygenException {
        if (Oxygen.getLib() == Lib.OPENGL) {
            modelData = new OGLModelData(model.getVertices(), model.getIndices(), model.getNormals(), model.getTextureCoords(), model.getTexturePath());
        } else if (Oxygen.getLib() == Lib.VULKAN) {
            modelData = new VulkanModelData(Oxygen.getVulkanModelIdCounter(), Collections.singletonList(new VulkanModelData.MeshData(model.getVertices(), model.getIndices())));
            Oxygen.incrementVulkanModelIdCounter();
        }
        if (!modelData.hasTexture()) {
            Oxygen.LOGGER.warn("Model has no texture");
        }
        if (!modelData.hasNormals()) {
            Oxygen.LOGGER.warn("Model has no normals");
        }
    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {

    }

    @Override
    public void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        if (modelData == null) return;
        OGLModel oglModel = OGLModel.transformModel(modelData);
        renderer.addModel(oglModel);
        if (oglModel.getModelData().hasTexture()) {
            renderer.getShaderProgram().setUniform("textureSampler", 0);
            renderer.getShaderProgram().setUniform("isTextured", true);
        }
        oglModel.getModelData().bind();
        glEnableVertexAttribArray(0);
        if (oglModel.getModelData().hasTexture()) glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, oglModel.getModelData().getIndicesCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        if (oglModel.getModelData().hasTexture()) glDisableVertexAttribArray(1);
        oglModel.getModelData().unbind();
        oglModel.cleanup();
    }

    public IModelData getModelData() {
        return modelData;
    }
}
