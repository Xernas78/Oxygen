package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.oxygen.model.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class SceneObject {

    private final List<Behavior> behaviors = new ArrayList<>();

    private Transform transform;
    private Model model;

    public void setTransform(Transform transform) {
        if (transform != null) {
            Behavior oldTransform = getBehavior(Transform.class);
            if (oldTransform != null) behaviors.remove(oldTransform);
            behaviors.add(transform);
        }
        this.transform = transform;
    }

    public void setModel(Model model) {
        if (model != null) {
            Behavior modelRenderer = getBehavior(ModelRenderer.class);
            if (modelRenderer != null) behaviors.remove(modelRenderer);
            behaviors.add(new ModelRenderer(model));
        }
        this.model = model;
    }

    public List<Behavior> getDefaultBehaviors() {
        List<Behavior> behaviors = new ArrayList<>();
        if (transform != null) behaviors.add(transform);
        if (model != null) behaviors.add(new ModelRenderer(model));
        return behaviors;
    }

    public abstract List<Behavior> getBehaviors();

    public final void startBehaviors(Oxygen oxygen) throws OxygenException {
        behaviors.addAll(getBehaviors());
        behaviors.addAll(getDefaultBehaviors());
        removeDuplicateBehaviors();
        for (Behavior behavior : behaviors) {
            behavior.start(oxygen, this);
        }
    }

    public final void updateBehaviors(Oxygen oxygen) {
        behaviors.forEach(behavior -> behavior.update(oxygen, this));
    }

    public final void inputBehaviors(Oxygen oxygen) {
        behaviors.forEach(behavior -> behavior.input(oxygen, oxygen.getWindow().getInput()));
    }

    public final void renderBehaviors(OGLRenderer renderer) throws OxygenException {
        for (Behavior behavior : behaviors) {
            behavior.render(renderer, this);
        }
    }

    public final <T> T getBehavior(Class<? extends Behavior> behaviorClass) {
        for (Behavior behavior : behaviors) {
            if (behavior.getClass().equals(behaviorClass)) {
                try {
                    return (T) behavior;
                } catch (ClassCastException ignore) {
                    return null;
                }
            }
        }
        return null;
    }

    private void removeDuplicateBehaviors() {
        for (int i = 0; i < behaviors.size(); i++) {
            for (int j = i + 1; j < behaviors.size(); j++) {
                if (behaviors.get(i).getClass().equals(behaviors.get(j).getClass())) {
                    behaviors.remove(j);
                    j--;
                }
            }
        }
    }
}