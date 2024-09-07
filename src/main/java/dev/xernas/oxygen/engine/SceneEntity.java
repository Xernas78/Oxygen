package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.engine.model.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class SceneEntity {

    private final List<Behavior> behaviors = new ArrayList<>();

    private Transform transform;
    private Model model;
    private String shaderName = "default";
    private boolean visible = true;

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

    public void setShader(String shaderName) {
        this.shaderName = shaderName;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<Behavior> getDefaultBehaviors() {
        List<Behavior> behaviors = new ArrayList<>();
        if (transform != null) behaviors.add(transform);
        if (model != null) behaviors.add(new ModelRenderer(model));
        return behaviors;
    }

    public abstract List<Behavior> getBehaviors();

    public final void awakeBehaviors(Oxygen oxygen) throws OxygenException {
        System.out.println("Awaking object "  + this.getClass().getSimpleName());
        behaviors.addAll(getBehaviors());
        behaviors.addAll(getDefaultBehaviors());
        removeDuplicateBehaviors();
        for (Behavior behavior : behaviors) {
            behavior.awake(oxygen, this);
        }
    }

    public final void startBehaviors(Oxygen oxygen) throws OxygenException {
        for (Behavior behavior : behaviors) behavior.start(oxygen, this);
    }

    public final void updateBehaviors(Oxygen oxygen) throws OxygenException {
        for (Behavior behavior : behaviors) behavior.update(oxygen, this);
    }

    public final void inputBehaviors(Oxygen oxygen) throws OxygenException {
        for (Behavior behavior : behaviors) behavior.input(oxygen, oxygen.getWindow().getInput());
    }

    public final void renderBehaviors(OGLRenderer renderer) throws OxygenException {
        for (Behavior behavior : behaviors) behavior.render(renderer, this);

    }

    public final void cleanupBehaviors(Oxygen oxygen) throws OxygenException {
        for (Behavior behavior : behaviors) behavior.cleanup(oxygen, this);
    }

    public String getShaderName() {
        return shaderName;
    }

    public boolean isVisible() {
        return visible;
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

    public static void instantiate(Oxygen oxygen, SceneEntity object) throws OxygenException {
        instantiate(oxygen, Oxygen.getCurrentScene(), object);
    }

    public static void instantiate(Oxygen oxygen, Scene scene, SceneEntity object) throws OxygenException {
        if (!Oxygen.isRunning()) {
            scene.addEntity(object);
            return;
        }
        scene.addEntity(object);
        object.awakeBehaviors(oxygen);
        object.startBehaviors(oxygen);
        oxygen.getRenderer().loadSceneEntity(object);
    }
}
