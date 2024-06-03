import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.model.ModelData;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements Behavior {

    @Override
    public void start(Oxygen oxygen) throws OxygenException {
        String modelId = "TriangleModel";
        ModelData.MeshData meshData = new ModelData.MeshData(new float[]{
                -0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f},
                new int[]{0, 1, 2});
        List<ModelData.MeshData> meshDataList = new ArrayList<>();
        meshDataList.add(meshData);
        ModelData modelData = new ModelData(modelId, meshDataList);
        List<ModelData> modelDataList = new ArrayList<>();
        modelDataList.add(modelData);
        oxygen.getRenderer().loadModels(modelDataList);
    }

    @Override
    public void update(Oxygen oxygen) {
        oxygen.getWindow().setTitle(oxygen.getWindow().getDefaultTitle() + " - FPS: " + oxygen.getFps());
    }

    @Override
    public void input(Oxygen oxygen) {

    }

    @Override
    public void cleanup() {

    }
}
