package dev.xernas.oxygen.engine.utils;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.function.Consumer;

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

    public static <T> T requireObject(SceneObject object, String error) throws OxygenException {
        if (object == null) {
            throw new OxygenException(error);
        }
        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw new OxygenException("Object is not the right type");
        }
    }

}
