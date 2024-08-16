import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.engine.camera.CameraController;
import dev.xernas.oxygen.render.utils.Lib;
import objects.GameManagerObj;
import objects.Light;
import objects.NormalObject;
import org.joml.Vector3f;

import java.awt.*;

public class OxygenTestApp {

    public static void main(String[] args) {
        Oxygen oxygen = new Oxygen.Builder()
                .applicationName("Oxygen Test App")
                .version("1.0.0")
                .title("On a tous besoin d'un Xernas dans la vie")
                .debug(false)
                .vsync(false)
                .resizable(true)
                .lib(Lib.OPENGL)
                .build();

        oxygen.addScene(new Scene()
                .addObject(new Camera(new CameraController(2f)))
                .addObject(new GameManagerObj())
                .addObject(new Light(new Transform(new Vector3f(0f, 10f, -10f)), Color.WHITE, Color.WHITE, 1f, true))
                .addObject(new NormalObject(new Transform(new Vector3f(0f, -3f, -15f), new Vector3f(0, 180, 0)), "dragon", "default"))
        );
        oxygen.init();
    }

}
