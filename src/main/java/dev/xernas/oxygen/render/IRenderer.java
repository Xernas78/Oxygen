package dev.xernas.oxygen.render;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.IOGLObject;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IRenderer extends IOxygenLogic {


    void render() throws OxygenException;

    void loadSceneObjects(List<SceneObject> sceneObjects) throws OxygenException;

    void loadSceneObject(SceneObject sceneObject) throws OxygenException;

    void clear();

}
