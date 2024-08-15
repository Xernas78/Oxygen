package dev.xernas.oxygen.engine.input;

public class KeyAction {

    private final Key key;
    private final Action action;

    public KeyAction(int key, int action) {
        this.key = Key.getKeyFromCode(key);
        this.action = Action.getActionFromCode(action);
    }

    public Key getKey() {
        return key;
    }

    public Action getAction() {
        return action;
    }
}
