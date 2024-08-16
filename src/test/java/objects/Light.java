package objects;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.LightSource;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.render.oxygen.model.Model;

import java.awt.*;
import java.util.List;

public class Light extends SceneObject {

    private final Color color;
    private final Color specularHighlight;
    private final float intensity;

    public Light(Transform position, Color color, Color specularHighlight, float intensity, boolean debug) {
        setTransform(position);
        if (debug) setModel(Model.CUBE.setMaterial(Material.DEBUG));
        this.color = color;
        this.specularHighlight = specularHighlight;
        this.intensity = intensity;
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of(new LightSource(color, specularHighlight, intensity));
    }
}
