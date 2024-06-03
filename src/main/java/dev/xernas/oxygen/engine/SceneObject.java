package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.ArrayList;
import java.util.List;

public interface SceneObject {

    List<Behavior> getBehaviors();

    default void startBehaviors(Oxygen oxygen) throws OxygenException {
        for (Behavior behavior : getBehaviors()) {
            behavior.start(oxygen);
        }
    }

    default void updateBehaviors(Oxygen oxygen) {
        getBehaviors().forEach(behavior -> behavior.update(oxygen));
    }

    default void inputBehaviors(Oxygen oxygen) {
        getBehaviors().forEach(behavior -> behavior.input(oxygen));
    }

    default void cleanupBehaviors() {
        getBehaviors().forEach(Behavior::cleanup);
    }
}
