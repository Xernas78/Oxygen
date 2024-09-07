package dev.xernas.oxygen.engine.scene;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.Scene;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.engine.material.Material;
import dev.xernas.oxygen.engine.model.Model;
import dev.xernas.oxygen.engine.model.Models;
import dev.xernas.oxygen.engine.entities.DrawableEntity;
import dev.xernas.oxygen.exception.OxygenException;

import java.util.List;

public class DrawableScene extends Scene {

    private final Oxygen oxygen;

    public DrawableScene(Oxygen oxygen) {
        super(false);
        this.oxygen = oxygen;
    }

    public DrawableEntity getSimpleDrawableEntity(Model model, float x, float y) {
        return new DrawableEntity(model, x, y) {
            @Override
            public List<Behavior> getBehaviors() {
                return List.of();
            }
        };
    }

    public DrawableEntity drawEntity(DrawableEntity drawableEntity) {
        try {
            SceneEntity.instantiate(oxygen, this, drawableEntity);
            return drawableEntity;
        } catch (OxygenException e) {
            Oxygen.LOGGER.fatal(new OxygenException("Error while drawing object"));
            return null;
        }
    }

    public DrawableEntity drawDot(float x, float y, float radius) {
        DrawableEntity entity = getSimpleDrawableEntity(Models.getCircle(radius), x, y);
        return drawEntity(entity);
    }

    public DrawableEntity drawQuad(float x, float y, float width, float height) {
        DrawableEntity entity = getSimpleDrawableEntity(Models.getQuad(width, height), x, y);
        return drawEntity(entity);
    }

    public DrawableEntity drawTexturedQuad(float x, float y, float width, float height, Material material) {
        DrawableEntity entity = getSimpleDrawableEntity(Models.getQuad(width, height).setMaterial(material), x, y);
        return drawEntity(entity);
    }

}
