package dev.xernas.oxygen.render.opengl.model;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModel;
import dev.xernas.oxygen.render.oxygen.model.interfaces.IModelData;

public class OGLModel implements IModel {

    private final OGLModelData OGLModelData;

    public OGLModel(OGLModelData OGLModelData) {
        this.OGLModelData = OGLModelData;
    }

    @Override
    public void init() throws OxygenException {
        OGLModelData.init();
    }

    @Override
    public void cleanup() throws OxygenException {
        OGLModelData.cleanup();
    }

    @Override
    public Integer getModelId() {
        return OGLModelData.getId();
    }

    public OGLModelData getModelData() {
        return OGLModelData;
    }

    public static OGLModel transformModel(IModelData modelData) throws OxygenException {
        OGLModelData OGLModelData = (OGLModelData) modelData;
        OGLModel OGLModel = new OGLModel(OGLModelData);
        OGLModel.init();
        return OGLModel;
    }
}
