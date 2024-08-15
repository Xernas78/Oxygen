package dev.xernas.oxygen.render.utils;

import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;

import java.util.List;

public class EmptyRenderer implements IRenderer {


    @Override
    public void render() throws OxygenException {

    }

    @Override
    public void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException {

    }

    @Override
    public void loadSceneObject(SceneObject sceneObject) throws OxygenException {

    }

    @Override
    public void init() throws OxygenException {

    }

    @Override
    public void cleanup() throws OxygenException {

    }
}
