package dev.xernas.oxygen.engine.utils;

import dev.xernas.oxygen.engine.Behavior;
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

}
