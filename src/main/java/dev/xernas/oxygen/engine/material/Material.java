package dev.xernas.oxygen.engine.material;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class Material {

    @NotNull
    public abstract Color getBaseColor();

    public abstract float getTransparency();

    public abstract boolean illuminable();

    public abstract float getReflectivity();

    public abstract float getReflectionVisibility();

    public abstract boolean backfaceCullingDisabled();
}
