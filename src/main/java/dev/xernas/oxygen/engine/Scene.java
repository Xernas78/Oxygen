package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
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
        for (SceneObject object : objects) {
            object.startBehaviors(oxygen);
        }
    }

    public void updateObjects(Oxygen oxygen) {
        objects.forEach(sceneObject -> sceneObject.updateBehaviors(oxygen));
    }

    public void inputObjects(Oxygen oxygen) {
        objects.forEach(sceneObject -> sceneObject.inputBehaviors(oxygen));
    }

    public void cleanupObjects() {
        objects.forEach(SceneObject::cleanupBehaviors);
    }

    public List<SceneObject> getObjects() {
        return objects;
    }

}
