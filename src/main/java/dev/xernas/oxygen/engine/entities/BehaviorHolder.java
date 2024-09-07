package dev.xernas.oxygen.engine.entities;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;

import java.util.ArrayList;
import java.util.List;

public class BehaviorHolder extends SceneEntity {

    private final List<Behavior> behaviors = new ArrayList<>();

    public BehaviorHolder(Behavior... behaviors) {
        this.behaviors.addAll(List.of(behaviors));
    }

    @Override
    public List<Behavior> getBehaviors() {
        return behaviors;
    }
}
