package dev.xernas.oxygen.engine.objects;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.material.DefaultMaterial;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.model.Models;

import java.util.List;

public class HUDPanel extends DrawableEntity {

    public HUDPanel() {
        super(Models.getQuad(0.5f, 0.5f).setMaterial(new DefaultMaterial()), 0f, 0f);
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of();
    }
}
