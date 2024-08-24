package dev.xernas.oxygen.engine.behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.utils.GlobalUtilitaries;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import org.joml.Vector3f;

import java.awt.*;

public class LightSource implements Behavior {

    public static final int MAX_LIGHTS = 10;
    public static int lightIndex = 0;

    private Transform transform;
    private Color color = Color.WHITE;
    private Color specularHighlight = Color.WHITE;
    private float intensity = 1f;

    public LightSource(Color color) {
        this.color = color;
    }

    public LightSource(Color color, Color specularHighlight) {
        this.color = color;
        this.specularHighlight = specularHighlight;
    }

    public LightSource(float intensity) {
        this.intensity = intensity;
    }

    public LightSource(Color color, Color specularHighlight, float intensity) {
        this.color = color;
        this.specularHighlight = specularHighlight;
        this.intensity = intensity;
    }

    public LightSource(Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSpecularHighlight(Color specularHighlight) {
        this.specularHighlight = specularHighlight;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public void awake(Oxygen oxygen, SceneObject parent) throws OxygenException {
        transform = GlobalUtilitaries.requireBehavior(parent.getBehavior(Transform.class), "LightSource behavior require Transform behavior");
    }

    @Override
    public void start(Oxygen oxygen, SceneObject parent) throws OxygenException {

    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {

    }

    @Override
    public void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        if (lightIndex < MAX_LIGHTS) {
            renderer.getCurrentShaderProgram().setUniform("lightPos[" + lightIndex + "]", transform.getPosition());
            renderer.getCurrentShaderProgram().setUniform("lightColor[" + lightIndex + "]", color);
            renderer.getCurrentShaderProgram().setUniform("lightIntensity[" + lightIndex + "]", intensity);
            renderer.getCurrentShaderProgram().setUniform("specularColor[" + lightIndex + "]", specularHighlight);
            lightIndex++;
        }
        renderer.getCurrentShaderProgram().setUniform("ambientLight", 0.15);
    }
}
