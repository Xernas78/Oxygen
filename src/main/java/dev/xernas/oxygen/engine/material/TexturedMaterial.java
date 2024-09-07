package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;

import java.nio.file.Path;

public abstract class TexturedMaterial extends Material {

    private boolean textured;

    public abstract String getSimplePath();

    public abstract int getTextureTiles();

    public boolean isTextured() {
        return textured;
    }

    public Path getTexturePath(boolean useRemoteResourceManager) {
        ResourceManager resourceManager = useRemoteResourceManager ? Oxygen.getRemoteResourceManager() : Oxygen.OXYGEN_RESOURCE_MANAGER;
        this.textured = getSimplePath() != null && !getSimplePath().isEmpty();
        Path fileTexturePath = resourceManager.getResourceAbsolutePath(resourceManager.getTexturesDir() + getSimplePath());
        return textured ? (fileTexturePath == null ? resourceManager.getResourceAbsolutePath(Oxygen.OXYGEN_RESOURCE_MANAGER.getTexturesDir() + "error.png") : fileTexturePath) : null;
    }
}
