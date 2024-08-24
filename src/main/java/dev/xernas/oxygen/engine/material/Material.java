package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;

import java.awt.*;
import java.nio.file.Path;

public class Material {

    public static Material DEFAULT = new Material(Color.decode("#f5deb3"));
    public static Material DEBUG = new Material(Color.GREEN, false);

    private final boolean textured;
    private final Path texturePath;
    private final Color baseColor;
    private final boolean illuminable;
    private final float reflectivity;
    private final float reflectionVisibility;

    public Material(ResourceManager resourceManager, String texturePath) {
        this(resourceManager, texturePath, Color.WHITE);
    }

    public Material(Color baseColor) {
        this(Oxygen.OXYGEN_RESOURCE_MANAGER, null, baseColor);
    }

    public Material(Color baseColor, boolean illuminable) {
        this(Oxygen.OXYGEN_RESOURCE_MANAGER, null, baseColor, illuminable, 0.5f, 1);
    }

    public Material(Color baseColor, float shininess) {
        this(Oxygen.OXYGEN_RESOURCE_MANAGER, null, baseColor, true, shininess, shininess * 15);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor) {
        this(resourceManager, texturePath, baseColor, true, 0.5f, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, boolean illuminable) {
        this(resourceManager, texturePath, baseColor, illuminable, 0.5f, 1);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, float shininess) {
        this(resourceManager, texturePath, baseColor, true, shininess, shininess * 15);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, float reflectivity, float reflectionVisibility) {
        this(resourceManager, texturePath, baseColor, true, reflectivity, reflectionVisibility);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, boolean illuminable, float shininess) {
        this(resourceManager, texturePath, baseColor, illuminable, shininess, shininess * 7.5f);
    }

    public Material(ResourceManager resourceManager, String texturePath, Color baseColor, boolean illuminable, float reflectivity, float reflectionVisibility) {
        this.textured = texturePath != null;
        Path fileTexturePath = resourceManager.getResourceAbsolutePath(resourceManager.getTexturesDir() + texturePath);
        this.texturePath = textured ? (fileTexturePath == null ? resourceManager.getResourceAbsolutePath(Oxygen.OXYGEN_RESOURCE_MANAGER.getTexturesDir() + "error.png") : fileTexturePath) : null;
        this.baseColor = baseColor;
        this.illuminable = illuminable;
        this.reflectivity = reflectivity;
        this.reflectionVisibility = reflectionVisibility;
    }

    public Material(Material material, boolean textured) {
        this.textured = textured;
        this.texturePath = textured ? material.texturePath : null;
        this.baseColor = material.baseColor;
        this.illuminable = material.illuminable;
        this.reflectivity = material.reflectivity;
        this.reflectionVisibility = material.reflectionVisibility;
    }

    public boolean isTextured() {
        return textured;
    }

    public Path getTexturePath() {
        return texturePath;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public boolean illuminable() {
        return illuminable;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public float getReflectionVisibility() {
        return reflectionVisibility;
    }
}
