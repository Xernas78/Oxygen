package dev.xernas.oxygen.render.opengl;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OpenGLException;

public interface IOGLObject extends IOxygenLogic {

    void bind() throws OpenGLException;

    void unbind() throws OpenGLException;

}
