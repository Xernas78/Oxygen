package dev.xernas.oxygen.render.opengl.model;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.model.IModel;
import dev.xernas.oxygen.render.model.IModelData;

import java.util.HashMap;
import java.util.Map;

public class OGLModel implements IModel {

    private static final Map<Integer, OGLModel> modelIdMap = new HashMap<>();

    private final OGLModelData oglModelData;

    public OGLModel(OGLModelData oglModelData) {
        this.oglModelData = oglModelData;
    }

    @Override
    public void init() throws OxygenException {
        oglModelData.init();
        modelIdMap.put(oglModelData.getId(), this);
    }

    @Override
    public void cleanup() throws OxygenException {
        oglModelData.cleanup();
    }

    @Override
    public Integer getModelId() {
        return oglModelData.getId();
    }

    public OGLModelData getModelData() {
        return oglModelData;
    }

    public static OGLModel byId(int id) {
        return modelIdMap.get(id);
    }

    public static OGLModel transformModel(IModelData modelData) throws OxygenException {
        OGLModelData finalOGLModelData = (OGLModelData) modelData;
        OGLModel finalOGLModel = new OGLModel(finalOGLModelData);
        finalOGLModel.init();
        return finalOGLModel;
    }
}
