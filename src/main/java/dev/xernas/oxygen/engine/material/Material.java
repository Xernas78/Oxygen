package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;

import java.awt.*;

public class Material {

    public static Material DEFAULT = new Material(Color.WHITE);
    public static Material DEBUG = new Material(Color.GRAY, false);

    private final ResourceManager resourceManager;
    private final String simpleTexturePath;
    private final String texturePath;
    private final Color baseColor;
    private final boolean illuminable;
    private final float shininess;
    private final float shineDamper;

    public Material(ResourceManager resourceManager, String texturePath) {
        this(resourceManager, texturePath, Color.WHITE);
    }

    public Material(Color baseColor) {
        this(Oxygen.OXYGEN_RESOURCE_MANAGER, null, baseColor);
    }

    public Material(Color baseColor, boolean illuminable) {
        this(Oxygen.OXYGEN_RESOURCE_MANAGER, null, baseColor, illuminable, 0, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor) {
        this(resourceManager, texturePath, baseColor, true, 0, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, boolean illuminable) {
        this(resourceManager, texturePath, baseColor, illuminable, 0, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, float shininess) {
        this(resourceManager, texturePath, baseColor, true, shininess, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, float shininess, float shineDamper) {
        this(resourceManager, texturePath, baseColor, true, shininess, shineDamper);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, boolean illuminable, float shininess, float shineDamper) {
        this.resourceManager = resourceManager;
        this.simpleTexturePath = texturePath;
        String fileTexturePath = resourceManager.getFileResourceAbsolutePath(resourceManager.getTexturesDir() + texturePath);
        this.texturePath = fileTexturePath == null && texturePath != null ? resourceManager.getFileResourceAbsolutePath(Oxygen.OXYGEN_RESOURCE_MANAGER.getTexturesDir() + "error.png") : fileTexturePath;
        this.baseColor = baseColor;
        this.illuminable = illuminable;
        this.shininess = shininess;
        this.shineDamper = shineDamper;
    }

    public String getSimpleTexturePath() {
        return simpleTexturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public boolean illuminable() {
        return illuminable;
    }

    public float getShininess() {
        return shininess;
    }

    public float getShineDamper() {
        return shineDamper;
    }
}
