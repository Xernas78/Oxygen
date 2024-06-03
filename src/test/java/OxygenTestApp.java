import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.exception.OxygenException;

public class OxygenTestApp {

    public static void main(String[] args) {
        Oxygen oxygen = new Oxygen.Builder()
                .applicationName("Oxygen Test App")
                .version("1.0.0")
                .title("Simploe le pire")
                .debug(false)
                .vsync(false)
                .resizable(false)
                .build();
        oxygen.addScene(new Scene().addObject(new TestObject()));
        try {
            oxygen.init();
        } catch (OxygenException e) {
            throw new RuntimeException(e);
        }
    }

}
