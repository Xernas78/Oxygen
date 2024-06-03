package dev.xernas.oxygen.engine;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OxygenException;

public interface Behavior {

    void start(Oxygen oxygen) throws OxygenException;

    void update(Oxygen oxygen);

    void input(Oxygen oxygen);

    void cleanup();

}
