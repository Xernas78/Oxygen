package objects;


import behaviors.ShowObject;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.resource.obj.OBJLoader;
import dev.xernas.oxygen.engine.utils.Direction;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;

public class NormalObject extends SceneObject {

    public NormalObject(Transform transform, String resource, String shader) {
        setTransform(transform);
        setModel(OBJLoader.getObjFromResource(resource, false).toModel(new Material(resource + ".png", Color.GREEN, 3f, 10)));
        setShader(shader);
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of(new ShowObject(Direction.UP, 2.5f));
    }
}
