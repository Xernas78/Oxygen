package objects;


import behaviors.ShowObject;
import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.resource.ModelLoader;
import dev.xernas.oxygen.engine.utils.Direction;

import java.awt.*;
import java.util.List;

public class NormalObject extends SceneObject {

    public NormalObject(Transform transform, String resourceObj, String resourceImg) {
        setTransform(transform);
        setModel(ModelLoader.loadFromResources(Oxygen.getRemoteResourceManager(), resourceObj, true)
                .toModel(new Material(
                        Oxygen.getRemoteResourceManager(),
                        resourceImg,
                        Color.YELLOW,
                        2f,
                        32f
                )));
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of(new ShowObject(Direction.UP, 2.5f));
    }
}
