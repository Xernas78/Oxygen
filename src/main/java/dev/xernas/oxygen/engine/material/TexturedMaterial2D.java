package dev.xernas.oxygen.engine.material;

public abstract class TexturedMaterial2D extends TexturedMaterial {

    @Override
    public boolean illuminable() {
        return false;
    }

    @Override
    public float getReflectivity() {
        return 0;
    }

    @Override
    public float getReflectionVisibility() {
        return 0;
    }

    @Override
    public boolean backfaceCullingDisabled() {
        return false;
    }


}
