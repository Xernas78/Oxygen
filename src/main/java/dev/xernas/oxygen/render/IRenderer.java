package dev.xernas.oxygen.render;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.List;

public interface IRenderer extends IOxygenLogic {


    void render() throws OxygenException;

    void loadSceneEntities(List<SceneEntity> sceneEntities) throws OxygenException;

    void loadSceneEntity(SceneEntity sceneEntity) throws OxygenException;

    void clear();

}
