package dev.xernas.oxygen.render.model;

import dev.xernas.oxygen.IOxygenLogic;

public interface IModelData extends IOxygenLogic {

    int getVertexCount();

    boolean hasTexture();
    boolean hasNormals();

}