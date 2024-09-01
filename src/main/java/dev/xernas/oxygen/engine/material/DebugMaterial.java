package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DebugMaterial extends Material {

    private final Color color;

    public DebugMaterial(Color color) {
        this.color = color;
    }

    @Override
    public @NotNull Color getBaseColor() {
        return color;
    }

    @Override
    public float getTransparency() {
        return 0;
    }

    @Override
    public boolean illuminable() {
        return false;
    }

    @Override
    public float getReflectivity() {
        return 0;
    }

    @Override
    public float getReflectionVisibility() {
        return 0;
    }

    @Override
    public boolean backfaceCullingDisabled() {
        return true;
    }
}
