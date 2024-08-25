package dev.xernas.oxygen.engine.objects;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleObject extends SceneObject {

    private final List<Behavior> behaviors = new ArrayList<>();

    public SimpleObject(Transform transform, Model model, Material material, Behavior... behaviors) {
        setTransform(transform);
        model.setMaterial(material);
        setModel(model);
        this.behaviors.addAll(Arrays.asList(behaviors));
    }

    @Override
    public List<Behavior> getBehaviors() {
        return behaviors;
    }
}
