package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final List<SceneObject> objects = new ArrayList<>();

    public Scene addObject(SceneObject object) {
        objects.add(object);
        return this;
    }

    public void startObjects(Oxygen oxygen) throws OxygenException {
        boolean hasCamera = false;
        for (SceneObject object : objects) {
            object.startBehaviors(oxygen);
            if (object.getClass().equals(Camera.class)) {
                hasCamera = true;
            }
        }
        if (!hasCamera) throw new OxygenException("Scene must have a camera");
        oxygen.getRenderer().loadSceneObjects(objects);
    }

    public void updateObjects(Oxygen oxygen) {
        objects.forEach(sceneObject -> sceneObject.updateBehaviors(oxygen));
    }

    public void inputObjects(Oxygen oxygen) {
        objects.forEach(sceneObject -> sceneObject.inputBehaviors(oxygen));
    }

    public Camera getCamera() {
        return getFirstObject(Camera.class);
    }

    public <T> T getFirstObject(Class<? extends SceneObject> objectClass) {
        for (SceneObject object : objects) {
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

    public List<SceneObject> getObjects() {
        return objects;
    }

    public <T> List<T> getObjects(Class<? extends SceneObject> objectClass) {
        List<T> objects = new ArrayList<>();
        for (SceneObject object : this.objects) {
            if (object.getClass().equals(objectClass)) {
                objects.add((T) object);
            }
        }
        return objects;
    }

}
