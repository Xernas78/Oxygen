package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DefaultMaterial extends Material {

    @Override
    public @NotNull Color getBaseColor() {
        return Color.decode("#f5deb3");
    }

    @Override
    public float getTransparency() {
        return 0;
    }

    @Override
    public boolean illuminable() {
        return true;
    }

    @Override
    public float getReflectivity() {
        return 1f;
    }

    @Override
    public float getReflectionVisibility() {
        return 1f;
    }

    @Override
    public boolean backfaceCullingDisabled() {
        return false;
    }
}
