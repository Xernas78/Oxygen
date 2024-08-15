package dev.xernas.oxygen.render.utils;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.engine.camera.CameraTransform;
import dev.xernas.oxygen.engine.utils.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TransformUtils {

    public static Matrix4f createTransformationMatrix(Transform transform) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity().translate(transform.getPosition())
                .rotateX((float) Math.toRadians(transform.getRotation().x))
                .rotateY((float) Math.toRadians(transform.getRotation().y))
                .rotateZ((float) Math.toRadians(transform.getRotation().z))
                .scale(transform.getScale());
        return matrix4f;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        CameraTransform transform = camera.getBehavior(CameraTransform.class);
        if (transform == null) throw new NullPointerException("Camera has no Transform behavior - FATAL (please contact a contributor)");
        Vector3f position = transform.getPosition();
        Vector3f rotation = transform.getRotation();
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), Direction.RIGHT)
                .rotate((float) Math.toRadians(rotation.y), Direction.UP)
                .rotate((float) Math.toRadians(rotation.z), Direction.FORWARD);
        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    //TODO Camera params
    public static Matrix4f createProjectionMatrix(Window window) {
        Camera camera = Oxygen.getCurrentScene().getCamera();
        return new Matrix4f().identity()
                .setPerspective(
                        (float) Math.toRadians(camera.getFov()),
                        (float) window.getWidth() / window.getHeight(),
                        camera.getzNear(),
                        camera.getzFar()
                );
    }

}