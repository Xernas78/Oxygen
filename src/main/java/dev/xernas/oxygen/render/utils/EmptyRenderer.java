package dev.xernas.oxygen.render.utils;

import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;

import java.util.List;

public class EmptyRenderer implements IRenderer {


    @Override
    public void render() throws OxygenException {

    }

    @Override
    public void loadSceneEntities(List<SceneEntity> sceneEntities) throws OxygenException {

    }

    @Override
    public void loadSceneEntity(SceneEntity sceneEntity) throws OxygenException {

    }

    @Override
    public void clear() {

    }

    @Override
    public void init() throws OxygenException {

    }

    @Override
    public void cleanup() throws OxygenException {

    }
}
