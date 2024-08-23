package dev.xernas.oxygen.engine.input;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;

import java.util.HashMap;
import java.util.Map;

public class Input {

    private final Window window;
    private final boolean azerty;
    private final Map<Key, Action> keyMap = new HashMap<>();

    public Input(Window window, boolean azerty) {
        this.window = window;
        this.azerty = azerty;
    }

    public void updateInput() {
        if (keyMap.isEmpty()) return;
        keyMap.clear();
    }

    public boolean keyPress(Key key) {
        return window.isKeyPressed(key);
    }

    public boolean keyRelease(Key key) {
        return keyMap.get(key) == Action.RELEASE;
    }

    public boolean keyHold(Key key) {
        return keyMap.get(key) == Action.HOLD;
    }

    public boolean keyIdle(Key key) {
        return keyMap.getOrDefault(key, Action.IDLE) == Action.IDLE;
    }

    public Action getKeyAction(Key key) {
        return keyMap.getOrDefault(key, Action.IDLE);
    }

    public boolean mousePress(Key button) {
        return window.isMouseButtonPressed(button);
    }

    public boolean mouseRelease(Key button) {
        return keyMap.get(button) == Action.RELEASE;
    }

    public boolean mouseIdle(Key button) {
        return keyMap.getOrDefault(button, Action.IDLE) == Action.IDLE;
    }

    public Action getMouseAction(Key button) {
        return keyMap.getOrDefault(button, Action.IDLE);
    }

    public void setKeyAction(Key key, Action action) {
        keyMap.put(key, action);
    }

    public boolean isAzerty() {
        return azerty;
    }
}
