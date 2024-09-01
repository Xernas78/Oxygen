package dev.xernas.oxygen.engine.objects;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.model.Model;
import org.joml.Vector3f;

import java.util.List;

public abstract class DrawableEntity extends SceneEntity {

    public DrawableEntity(Model model) {
        this(model, new Transform());
    }

    public DrawableEntity(Model model, float x, float y) {
        this(model, new Transform(new Vector3f(x, y, 0)));
    }

    public DrawableEntity(Model model, Transform transform) {
        setModel(model);
        setTransform(transform);
        setShader("ui");
    }

    @Override
    public abstract List<Behavior> getBehaviors();
}
