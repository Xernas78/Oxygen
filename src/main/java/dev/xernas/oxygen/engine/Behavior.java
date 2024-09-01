package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;

public interface Behavior {

    default void awake(Oxygen oxygen, SceneEntity parent) throws OxygenException {
        // Do nothing
    };

    void start(Oxygen oxygen, SceneEntity parent) throws OxygenException;

    void update(Oxygen oxygen, SceneEntity parent) throws OxygenException;

    default void input(Oxygen oxygen, Input input) throws OxygenException {
        // Do nothing
    };

    default void render(OGLRenderer renderer, SceneEntity parent) throws OxygenException {
        // Do nothing
    }

    default void cleanup(Oxygen oxygen, SceneEntity parent) throws OxygenException {
        // Do nothing
    }

}
