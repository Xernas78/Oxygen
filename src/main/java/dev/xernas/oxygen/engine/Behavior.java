package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;

import java.util.Scanner;

public interface Behavior {

    void start(Oxygen oxygen, SceneObject parent) throws OxygenException;

    void update(Oxygen oxygen, SceneObject parent);

    default void input(Oxygen oxygen, Input input) {
        // Do nothing
    };

    default void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        // Do nothing
    }

}
