package dev.xernas.oxygen.engine.entities;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleEntity extends SceneEntity {

    private final List<Behavior> behaviors = new ArrayList<>();

    public SimpleEntity(Model model) {
        setModel(model);
    }

    public SimpleEntity(Transform transform, Model model) {
        setTransform(transform);
        setModel(model);
    }

    public SimpleEntity(Transform transform, Model model, Material material, String shader, Behavior... behaviors) {
        setTransform(transform);
        model.setMaterial(material != null ? material : model.getMaterial());
        setModel(model);
        setShader(shader);
        this.behaviors.addAll(Arrays.asList(behaviors));
    }

    @Override
    public List<Behavior> getBehaviors() {
        return behaviors;
    }
}
