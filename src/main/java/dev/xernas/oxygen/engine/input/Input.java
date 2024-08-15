package dev.xernas.oxygen.engine.input;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;

import java.util.function.Consumer;

public class Input {

    private final Window window;

    public Input(Window window) {
        this.window = window;
    }

    public boolean isKeyPressed(Key key) {
        return window.isKeyPressed(key);
    }

}
