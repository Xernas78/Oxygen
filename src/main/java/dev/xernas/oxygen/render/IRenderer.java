package dev.xernas.oxygen.render;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.List;

public interface IRenderer extends IOxygenLogic {


    void render() throws OxygenException;

    void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException;

    void loadSceneObject(SceneObject sceneObject) throws OxygenException;

}
