package dev.xernas.oxygen.engine.camera;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Camera extends SceneObject {

    private CameraController controller;

    private final Vector3f position;
    private final Vector3f rotation;
    private final float zNear;
    private final float zFar;
    private final int fov;

    //TODO Camera speed
    public Camera() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = 90;
    }

    public Camera(float zNear, float zFar) {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = 90;
    }

    public Camera(int fov) {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = fov;
    }

    public Camera(int fov, float zNear, float zFar) {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
    }

    public Camera(Vector3f position) {
        this.position = position;
        this.rotation = new Vector3f();
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = 90;
    }

    public Camera(Vector3f position, int fov) {
        this.position = position;
        this.rotation = new Vector3f();
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = fov;
    }

    public Camera(Vector3f position, float zNear, float zFar) {
        this.position = position;
        this.rotation = new Vector3f();
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = 90;
    }

    public Camera(Vector3f position, int fov, float zNear, float zFar) {
        this.position = position;
        this.rotation = new Vector3f();
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = 90;
    }

    public Camera(Vector3f position, Vector3f rotation, int fov) {
        this.position = position;
        this.rotation = rotation;
        this.zNear = 0.01f;
        this.zFar = 1000f;
        this.fov = fov;
    }

    public Camera(Vector3f position, Vector3f rotation, float zNear, float zFar) {
        this.position = position;
        this.rotation = rotation;
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = 90;
    }

    public Camera(Vector3f position, Vector3f rotation, int fov, float zNear, float zFar) {
        this.position = position;
        this.rotation = rotation;
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
    }

    public Camera(CameraController controller) {
        this();
        this.controller = controller;
    }

    public Camera(int fov, CameraController controller) {
        this(fov);
        this.controller = controller;
    }

    public Camera(float zNear, float zFar, CameraController controller) {
        this(zNear, zFar);
        this.controller = controller;
    }

    public Camera(int fov, float zNear, float zFar, CameraController controller) {
        this(fov, zNear, zFar);
        this.controller = controller;
    }

    public Camera(Vector3f position, CameraController controller) {
        this(position);
        this.controller = controller;
    }

    public Camera(Vector3f position, int fov, CameraController controller) {
        this(position, fov);
        this.controller = controller;
    }

    public Camera(Vector3f position, float zNear, float zFar, CameraController controller) {
        this(position, zNear, zFar);
        this.controller = controller;
    }

    public Camera(Vector3f position, int fov, float zNear, float zFar, CameraController controller) {
        this(position, fov, zNear, zFar);
        this.controller = controller;
    }

    public Camera(Vector3f position, Vector3f rotation, CameraController controller) {
        this(position, rotation);
        this.controller = controller;
    }

    public Camera(Vector3f position, Vector3f rotation, int fov, CameraController controller) {
        this(position, rotation, fov);
        this.controller = controller;
    }

    public Camera(Vector3f position, Vector3f rotation, float zNear, float zFar, CameraController controller) {
        this(position, rotation, zNear, zFar);
        this.controller = controller;
    }

    public Camera(Vector3f position, Vector3f rotation, int fov, float zNear, float zFar, CameraController controller) {
        this(position, rotation, fov, zNear, zFar);
        this.controller = controller;
    }

    @Override
    public List<Behavior> getDefaultBehaviors() {
        return List.of(new CameraTransform(position, rotation));
    }

    @Override
    public List<Behavior> getBehaviors() {
        List<Behavior> behaviors = new ArrayList<>();
        if (controller != null) behaviors.add(controller);
        return behaviors;
    }

    public int getFov() {
        return fov;
    }

    public float getzNear() {
        return zNear;
    }

    public float getzFar() {
        return zFar;
    }
}
