package dev.xernas.oxygen.engine.utils;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.exception.OxygenException;

public class GlobalUtilitaries {

    public static <T> T requireBehavior(Behavior behavior, String error) throws OxygenException {
        if (behavior == null) {
            throw new OxygenException(error);
        }
        try {
            return (T) behavior;
        } catch (ClassCastException e) {
            throw new OxygenException("Behavior is not the right type");
        }
    }

    public static <T> T requireObject(SceneEntity object, String error) throws OxygenException {
        if (object == null) {
            throw new OxygenException(error);
        }
        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw new OxygenException("Object is not the right type");
        }
    }

    public static int requireNotEquals(int obj, int notEqual, String message) throws OxygenException {
        if (obj == notEqual) throw new OxygenException(message);
        return obj;
    }

}
