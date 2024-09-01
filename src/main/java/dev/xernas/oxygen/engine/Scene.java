package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.exception.OxygenException;

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

    private final List<SceneEntity> objects = new ArrayList<>();

    public void addObject(SceneEntity object) {
        objects.add(object);
    }

    public Scene addObjects(SceneEntity... objects) {
        for (SceneEntity object : objects) {
            addObject(object);
        }
        return this;
    }

    public void load(Oxygen oxygen) throws OxygenException {
        awakeObjects(oxygen);
        startObjects(oxygen);
    }

    public void awakeObjects(Oxygen oxygen) throws OxygenException {
        boolean hasCamera = false;
        for (SceneEntity object : objects) {
            object.awakeBehaviors(oxygen);
            if (object.getClass().equals(Camera.class)) {
                hasCamera = true;
            }
        }
        if (!hasCamera && needsCamera) throw new OxygenException("Scene must have a camera");
        oxygen.getRenderer().loadSceneObjects(objects);
    }

    public void startObjects(Oxygen oxygen) throws OxygenException {
        for (SceneEntity object : objects) object.startBehaviors(oxygen);
    }

    public void updateObjects(Oxygen oxygen) throws OxygenException {
        List<SceneEntity> updateObjects = new ArrayList<>(objects);
        for (SceneEntity object : updateObjects) object.updateBehaviors(oxygen);
    }

    public void inputObjects(Oxygen oxygen) throws OxygenException {
        List<SceneEntity> inputObjects = new ArrayList<>(objects);
        for (SceneEntity object : inputObjects) object.inputBehaviors(oxygen);
    }

    public void cleanupObjects(Oxygen oxygen) throws OxygenException {
        for (SceneEntity object : objects) object.cleanupBehaviors(oxygen);
    }

    public Camera getCamera() {
        Camera camera = getFirstObject(Camera.class);
        return camera != null ? camera : new Camera();
    }

    public <T> T getFirstObject(Class<? extends SceneEntity> objectClass) {
        for (SceneEntity object : objects) {
            if (object.getClass().equals(objectClass)) {
                try {
                    return (T) object;
                } catch (ClassCastException ignore) {
                    return null;
                }
            }
        }
        return null;
    }

    public List<SceneEntity> getObjects() {
        return new ArrayList<>(this.objects);
    }

    public <T> List<T> getObjects(Class<? extends SceneEntity> objectClass) {
        List<T> objects = new ArrayList<>();
        List<SceneEntity> objectsList = getObjects();
        for (SceneEntity object : objectsList) {
            if (object.getClass().equals(objectClass)) {
                objects.add((T) object);
            }
        }
        return objects;
    }

}
