import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;

import java.util.List;

public class TestObject implements SceneObject {

    @Override
    public List<Behavior> getBehaviors() {
        return List.of(new GameManager());
    }

}
