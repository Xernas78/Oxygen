package behaviors;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.input.Input;
import dev.xernas.oxygen.engine.input.Key;
import dev.xernas.oxygen.exception.OxygenException;
import objects.NormalObject;

import java.awt.*;
import java.util.List;

public class GameManager implements Behavior {

    private static Window window;


    @Override
    public void start(Oxygen oxygen, SceneObject parent) throws OxygenException {
        window = oxygen.getWindow();
    }

    @Override
    public void update(Oxygen oxygen, SceneObject parent) {
        window.setTitle(oxygen.getWindow().getDefaultTitle() + " - FPS: " + Oxygen.getFps());
        window.setBackgroundColor(Color.decode("#000060"));
    }

    @Override
    public void input(Oxygen oxygen, Input input) {
        if (input.isKeyPressed(Key.KEY_ESCAPE)) {
            oxygen.stop();
        }
    }
}
