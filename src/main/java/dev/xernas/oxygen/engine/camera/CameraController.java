package dev.xernas.oxygen.engine.camera;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.engine.input.Key;
import dev.xernas.oxygen.engine.utils.GlobalUtilitaries;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.math.MathUtils;
import org.joml.Vector3f;

public class CameraController implements Behavior {

    private static CameraTransform cameraTransform;

    private static Vector3f direction = new Vector3f();
    private static Vector3f rotation = new Vector3f();

    private final float speed;

    public CameraController() {
        this.speed = 0.01f;
    }

    public CameraController(float speed) {
        this.speed = speed * 0.01f;
    }

    @Override
    public void awake(Oxygen oxygen, SceneEntity parent) throws OxygenException {
        cameraTransform = GlobalUtilitaries.requireBehavior(parent.getBehavior(CameraTransform.class), "CameraController requires a CameraTransform behavior");
    }

    @Override
    public void start(Oxygen oxygen, SceneEntity parent) throws OxygenException {

    }

    @Override
    public void update(Oxygen oxygen, SceneEntity parent) {
        cameraTransform.move(direction);
        cameraTransform.rotate(rotation);
        // Clamp the camera rotation
        Vector3f rotation = cameraTransform.getRotation();
        rotation.x = MathUtils.clamp(rotation.x, -90, 90);
        cameraTransform.setRotation(rotation);
    }

    @Override
    public void input(Oxygen oxygen, Input input) {
        direction = new Vector3f();
        rotation = new Vector3f();
        if (input.keyPress(Key.KEY_Z)) {
            direction.add(new Vector3f(0, 0, -speed));
        }
        if (input.keyPress(Key.KEY_S)) {
            direction.add(new Vector3f(0, 0, speed));
        }
        if (input.keyPress(Key.KEY_Q)) {
            direction.add(new Vector3f(-speed, 0, 0));
        }
        if (input.keyPress(Key.KEY_D)) {
            direction.add(new Vector3f(speed, 0, 0));
        }
        if (input.keyPress(Key.KEY_SPACE)) {
            direction.add(new Vector3f(0, speed, 0));
        }
        if (input.keyPress(Key.KEY_LEFT_SHIFT)) {
            direction.add(new Vector3f(0, -speed, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_UP)) {
            rotation.add(new Vector3f(-0.1f, 0, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_DOWN)) {
            rotation.add(new Vector3f(0.1f, 0, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_LEFT)) {
            rotation.add(new Vector3f(0, -0.1f, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_RIGHT)) {
            rotation.add(new Vector3f(0, 0.1f, 0));
        }
    }
}
