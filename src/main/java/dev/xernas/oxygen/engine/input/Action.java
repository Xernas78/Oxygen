package dev.xernas.oxygen.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public enum Action {

    PRESS(GLFW_PRESS),
    RELEASE(GLFW_RELEASE);

    private final int actionCode;

    Action(int actionCode) {
        this.actionCode = actionCode;
    }

    public static Action getActionFromCode(int code) {
        for (Action action : values()) {
            if (action.actionCode == code) {
                return action;
            }
        }
        return null;
    }
}
