package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Scene {

    private final List<SceneObject> objects = new ArrayList<>();

    public void addObject(SceneObject object) {
        objects.add(object);
    }

    public Scene addObjects(SceneObject... objects) {
        for (SceneObject object : objects) {
            addObject(object);
        }
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

    public void updateObjects(Oxygen oxygen) throws OxygenException {
        List<SceneObject> updateObjects = new ArrayList<>(objects);
        for (SceneObject object : updateObjects) object.updateBehaviors(oxygen);
    }

    public void inputObjects(Oxygen oxygen) throws OxygenException {
        List<SceneObject> inputObjects = new ArrayList<>(objects);
        for (SceneObject object : inputObjects) object.inputBehaviors(oxygen);
    }

    public void cleanupObjects(Oxygen oxygen) throws OxygenException {
        for (SceneObject object : objects) object.cleanupBehaviors(oxygen);
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
