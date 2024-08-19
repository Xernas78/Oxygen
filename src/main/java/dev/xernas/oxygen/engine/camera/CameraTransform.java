package dev.xernas.oxygen.engine.camera;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.OGLRenderer;
import dev.xernas.oxygen.render.utils.TransformUtils;
import org.joml.Vector3f;

public class CameraTransform extends Transform {

    public CameraTransform() {
        super();
    }

    public CameraTransform(Vector3f position) {
        super(position);
    }

    public CameraTransform(Vector3f position, Vector3f rotation) {
        super(position, rotation);
    }

    @Override
    public void move(Vector3f offset) {
        if (offset.z != 0) {
            getPosition().x += (float) Math.sin(Math.toRadians(getRotation().y)) * -1.0f * offset.z;
            getPosition().z += (float) Math.cos(Math.toRadians(getRotation().y)) * offset.z;
        }
        if (offset.x != 0) {
            getPosition().x += (float) Math.sin(Math.toRadians(getRotation().y - 90)) * -1.0f * offset.x;
            getPosition().z += (float) Math.cos(Math.toRadians(getRotation().y - 90)) * offset.x;
        }
        getPosition().y += offset.y;
    }

    @Override
    public void render(OGLRenderer renderer, SceneObject parent) throws OxygenException {
        super.render(renderer, parent);
        renderer.getCurrentShaderProgram().setUniform("viewMatrix", TransformUtils.createViewMatrix(this));
    }
}
