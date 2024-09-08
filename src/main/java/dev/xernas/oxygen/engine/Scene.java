package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.model.OGLModelData;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final boolean needsCamera;

    public Scene() {
        this(true);
    }

    public Scene(boolean needsCamera) {
        this.needsCamera = needsCamera;
    }

    private final List<SceneEntity> entities = new ArrayList<>();

    public void addEntity(SceneEntity object) {
        entities.add(object);
    }

    public Scene addEntities(SceneEntity... entities) {
        for (SceneEntity entity : entities) {
            addEntity(entity);
        }
        return this;
    }

    public void load(Oxygen oxygen) throws OxygenException {
        awakeEntities(oxygen);
        startEntities(oxygen);
    }

    public void awakeEntities(Oxygen oxygen) throws OxygenException {
        boolean hasCamera = false;
        for (SceneEntity entity : entities) {
            entity.awakeBehaviors(oxygen);
            if (entity.getClass().equals(Camera.class)) {
                hasCamera = true;
            }
        }
        if (!hasCamera && needsCamera) throw new OxygenException("Scene must have a camera");
        oxygen.getRenderer().loadSceneEntities(entities);
    }

    public void startEntities(Oxygen oxygen) throws OxygenException {
        for (SceneEntity entity : entities) entity.startBehaviors(oxygen);
    }

    public void updateEntities(Oxygen oxygen) throws OxygenException {
        List<SceneEntity> updateEntities = new ArrayList<>(entities);
        for (SceneEntity entity : updateEntities) entity.updateBehaviors(oxygen);
    }

    public void inputEntities(Oxygen oxygen) throws OxygenException {
        List<SceneEntity> inputEntities = new ArrayList<>(entities);
        for (SceneEntity entity : inputEntities) entity.inputBehaviors(oxygen);
    }

    public void cleanupEntities(Oxygen oxygen) throws OxygenException {
        for (SceneEntity entity : entities) entity.cleanupBehaviors(oxygen);
        OGLModelData.clearClean();
    }

    public void setShader(String shaderName) {
        for (SceneEntity entity : entities) {
            entity.setShader(shaderName);
        }
    }

    public Camera getCamera() {
        Camera camera = getFirstEntity(Camera.class);
        return camera != null ? camera : new Camera();
    }

    public <T> T getFirstEntity(Class<? extends SceneEntity> objectClass) {
        for (SceneEntity entity : entities) {
            if (entity.getClass().equals(objectClass)) {
                try {
                    return (T) entity;
                } catch (ClassCastException ignore) {
                    return null;
                }
            }
        }
        return null;
    }

    public List<SceneEntity> getEntities() {
        return new ArrayList<>(this.entities);
    }

    public <T> List<T> getEntities(Class<? extends SceneEntity> objectClass) {
        List<T> entities = new ArrayList<>();
        List<SceneEntity> entityList = getEntities();
        for (SceneEntity object : entityList) {
            if (object.getClass().equals(objectClass)) {
                entities.add((T) object);
            }
        }
        return entities;
    }

}
