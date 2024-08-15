package objects;

import behaviors.GameManager;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;

import java.util.List;

public class GameManagerObj extends SceneObject {

    @Override
    public List<Behavior> getBehaviors() {
        return List.of(new GameManager());
    }
}
