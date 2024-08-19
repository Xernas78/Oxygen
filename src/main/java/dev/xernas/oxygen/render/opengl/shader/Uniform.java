package dev.xernas.oxygen.render.opengl.shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.awt.*;

import static org.lwjgl.opengl.GL20.*;

public class Uniform<T> {

    private final String name;
    private final int location;
    private T value;

    public Uniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public void setValue(T value) {
        this.value = value;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (value instanceof Integer) {
                glUniform1i(location, (Integer) value);
            } else if (value instanceof Float) {
                glUniform1f(location, (Float) value);
            } else if (value instanceof Double) {
                glUniform1f(location, ((Double) value).floatValue());
            } else if (value instanceof Boolean) {
                glUniform1i(location, (Boolean) value ? 1 : 0);
            } else if (value instanceof Matrix4f) {
                glUniformMatrix4fv(location, false, ((Matrix4f) value).get(stack.mallocFloat(16)));
            } else if (value instanceof Vector3f v) {
                glUniform3f(location, v.x, v.y, v.z);
            } else if (value instanceof Color color) {
                glUniform3f(location, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getLocation() {
        return location;
    }

    public T getValue() {
        return value;
    }
}
