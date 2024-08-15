package dev.xernas.oxygen.render.math;

import org.joml.Vector3f;

public class MathUtils {

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Vector3f multiply(Vector3f vector, float scalar) {
        return new Vector3f(vector.x * scalar, vector.y * scalar, vector.z * scalar);
    }

    public static Vector3f multiply(Vector3f vector, int number) {
        return new Vector3f(vector.x * number, vector.y * number, vector.z * number);
    }

}
