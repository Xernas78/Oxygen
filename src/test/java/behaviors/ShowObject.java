package behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.math.MathUtils;
import org.joml.Vector3f;

public class ShowObject implements Behavior {

    private Transform transform;

    private final Vector3f rotate;
    private final float speed;

    public ShowObject(Vector3f rotate, float speed) {
        this.rotate = rotate;
        this.speed = speed;
    }

    @Override
    public void start(Oxygen oxygen, SceneObject parent) throws OxygenException {
        transform = parent.getBehavior(Transform.class);
        if (transform == null) {
            throw new OxygenException("Transform behavior not found");
        }
    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {
        transform.rotate(MathUtils.multiply(rotate, speed * 0.01f));
    }
}
