import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.camera.Camera;
import dev.xernas.oxygen.engine.camera.CameraController;
import dev.xernas.oxygen.render.utils.Lib;
import objects.GameManagerObj;
import objects.NormalObject;
import org.joml.Vector3f;

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
                .addObject(new Camera())
                .addObject(new GameManagerObj())
                .addObject(new NormalObject(new Transform(new Vector3f(-10f, -3f, -15f), new Vector3f(0, 180, 0)), "stall"))
                .addObject(new NormalObject(new Transform(new Vector3f(10f, -3f, -15f), new Vector3f(0, 180, 0)).scale(0.4f), "sword"))
        );
        oxygen.init();
    }

}
