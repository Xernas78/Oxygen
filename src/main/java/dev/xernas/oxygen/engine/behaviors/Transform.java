package dev.xernas.oxygen.engine.behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.utils.TransformUtils;
import org.joml.Vector3f;

public class Transform implements Behavior {

    private final Vector3f defaultPos;
    private final Vector3f defaultRot;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = 1;
        this.defaultPos = new Vector3f(0, 0, 0);
        this.defaultRot = new Vector3f(0, 0, 0);
    }

    public Transform(Vector3f position) {
        this.position = position;
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = 1;
        this.defaultPos = position;
        this.defaultRot = new Vector3f(0, 0, 0);
    }

    public Transform(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = 1;
        this.defaultPos = position;
        this.defaultRot = rotation;
    }

    public Transform(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.defaultPos = position;
        this.defaultRot = rotation;
    }

    @Override
    public void start(Oxygen oxygen, SceneEntity parent) throws OxygenException {

    }

    @Override
    public void update(Oxygen oxygen, SceneEntity parent) {

    }

    @Override
    public void render(OGLRenderer renderer, SceneEntity parent) throws OxygenException {
        renderer.getCurrentShaderProgram().setUniform("transformMatrix", TransformUtils.createTransformationMatrix(this));
    }

    public void move(Vector3f position) {
        this.position.add(position);
    }

    public void incPosition(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public void rotate(Vector3f rotation) {
        this.rotation.add(rotation);
    }

    public void incRotation(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public Transform scale(float scale) {
        this.scale = scale;
        return this;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getOffsetPosition(Vector3f offset) {
        return new Vector3f(defaultPos).add(offset);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getOffsetRotation(Vector3f offset) {
        return new Vector3f(defaultRot).add(offset);
    }

    public float getScale() {
        return scale;
    }
}
