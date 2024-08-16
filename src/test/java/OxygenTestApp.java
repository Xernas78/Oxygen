import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.engine.camera.CameraController;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.render.utils.Lib;
import objects.GameManagerObj;
import objects.Light;
import objects.NormalObject;
import org.joml.Vector3f;

import java.awt.*;

public class OxygenTestApp {

    public static void main(String[] args) {
        Oxygen.Builder oxygenBuilder = new Oxygen.Builder(
                "On a tous besoin d'un Xernas dans la vie",
                    new ResourceManager(OxygenTestApp.class, "shaders/", "models/", "textures/")
        );
        oxygenBuilder.applicationName("Oxygen Test App");
        oxygenBuilder.version("1.0.0");
        oxygenBuilder.debug(false);
        oxygenBuilder.vsync(false);
        oxygenBuilder.resizable(true);
        oxygenBuilder.lib(Lib.OPENGL);

        Oxygen oxygen = oxygenBuilder.build();

        oxygen.addScene(new Scene()
                .addObject(new Camera(new CameraController(2f)))
                .addObject(new GameManagerObj())
                .addObject(new Light(new Transform(new Vector3f(0f, 10f, 100f)), Color.WHITE, Color.WHITE, 1f, false))
                .addObject(new NormalObject(new Transform(new Vector3f(0f, -3f, -15f), new Vector3f(0, 180, 0)), "dragon.obj", "gold.jpg"))
        );
        oxygen.init();
    }

}
