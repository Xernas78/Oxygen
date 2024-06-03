package dev.xernas.oxygen;

import dev.xernas.oxygen.exception.OxygenException;

public interface IOxygenLogic {

    void init() throws OxygenException;

    void cleanup() throws OxygenException;

}
