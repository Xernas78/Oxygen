package dev.xernas.oxygen.engine.objects;

import dev.xernas.oxygen.engine.Behavior;
import dev.xernas.oxygen.engine.model.Model;
import dev.xernas.oxygen.engine.model.Models;

import java.util.List;

public class TexturedHUD extends DrawableEntity {

    public TexturedHUD() {
        super(Models.getQuad(1f, 1f), 0, 0);
    }

    public TexturedHUD(float width, float height, float x, float y) {
        super(Models.getQuad(width, height), x, y);
    }

    @Override
    public List<Behavior> getBehaviors() {
        return List.of();
    }
}
