package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public abstract class TexturedMaterial extends Material {

    private boolean textured;

    public abstract @NotNull ResourceManager getResourceManager();

    public abstract String getSimplePath();

    public abstract int getTextureTiles();

    public boolean isTextured() {
        return textured;
    }

    public Path getTexturePath() {
        this.textured = getSimplePath() != null && !getSimplePath().isEmpty();
        Path fileTexturePath = getResourceManager().getResourceAbsolutePath(getResourceManager().getTexturesDir() + getSimplePath());
        return textured ? (fileTexturePath == null ? getResourceManager().getResourceAbsolutePath(Oxygen.OXYGEN_RESOURCE_MANAGER.getTexturesDir() + "error.png") : fileTexturePath) : null;
    }
}
