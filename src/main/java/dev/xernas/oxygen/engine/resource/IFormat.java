package dev.xernas.oxygen.engine.resource;

import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.render.oxygen.model.Model;

public interface IFormat {

    Model toModel(Material material);

    default Model toModel() {
        return toModel(Material.DEFAULT);
    }

}
