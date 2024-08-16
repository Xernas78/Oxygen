package dev.xernas.oxygen.engine.material;

import dev.xernas.oxygen.Oxygen;

import java.awt.*;

public class Material {

    public static Material DEFAULT = new Material(Color.WHITE);
    public static Material DEBUG = new Material(Color.GRAY, false);

    private final String texturePath;
    private final Color baseColor;
    private final boolean illuminable;
    private final float shininess;
    private final float shineDamper;

    public Material(String texturePath) {
        this(texturePath, Color.WHITE);
    }

    public Material(Color baseColor) {
        this(null, baseColor);
    }

    public Material(Color baseColor, boolean illuminable) {
        this(null, baseColor, illuminable, 0, 1);
    }

    public Material(String texturePath, Color baseColor) {
        this(texturePath, baseColor, true, 0, 1);
    }

    public Material(String texturePath, Color baseColor, boolean illuminable) {
        this(texturePath, baseColor, illuminable, 0, 1);
    }

    public Material(String texturePath, Color baseColor, float shininess) {
        this(texturePath, baseColor, true, shininess, 1);
    }

    public Material(String texturePath, Color baseColor, float shininess, float shineDamper) {
        this(texturePath, baseColor, true, shininess, shineDamper);
    }

    public Material(String texturePath, Color baseColor, boolean illuminable, float shininess, float shineDamper) {
        this.texturePath = texturePath;
        this.baseColor = baseColor;
        this.illuminable = illuminable;
        this.shininess = shininess;
        this.shineDamper = shineDamper;
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
