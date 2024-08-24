package dev.xernas.oxygen.engine.terrain;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.SceneObject;
import dev.xernas.oxygen.engine.behaviors.Transform;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.model.Model;
import dev.xernas.oxygen.engine.model.Models;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;

public class Terrain extends SceneObject {

    public Terrain(Vector3f position, int size) {
        this(position, 128, size);
    }

    public Terrain(Vector3f position, int vertexCount, int size) {
        this(position, vertexCount, size, null, 1);
    }

    public Terrain(Vector3f position, int vertexCount, int size, String texture, int numTiles) {
        setTransform(new Transform(position));
        Model terrainModel = Models.getPlane(vertexCount, size);
        if (texture != null) terrainModel.setMaterial(new Material(Oxygen.getRemoteResourceManager(), texture, numTiles, Color.WHITE));
        setModel(terrainModel);
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of();
    }
}
