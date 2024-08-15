package dev.xernas.oxygen.render.opengl.shader;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.IOGLObject;
import dev.xernas.oxygen.render.opengl.utils.OGLUtils;
import org.lwjgl.opengl.GL20;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class OGLShaderProgram implements IOGLObject {

    public static final OGLShaderProgram DEFAULT = new OGLShaderProgram("default");

    private int programId;

    private final String shaderName;
    private final String vertexShader;
    private final String fragmentShader;

    public OGLShaderProgram(String shaderName) {
        this.shaderName = shaderName;
        this.vertexShader = shaderName + ".vert";
        this.fragmentShader = shaderName + ".frag";
    }

    @Override
    public void bind() throws OpenGLException {
        glUseProgram(programId);
    }

    @Override
    public void unbind() throws OpenGLException {
        glUseProgram(0);
    }

    @Override
    public void init() throws OxygenException {
        int vertexShaderId = OGLUtils.requireNotEquals(glCreateShader(GL_VERTEX_SHADER), 0, "Error creating vertex shader");
        int fragmentShaderId = OGLUtils.requireNotEquals(glCreateShader(GL_FRAGMENT_SHADER), 0, "Error creating fragment shader");
        String vertexShaderCode = readShader(vertexShader);
        String fragmentShaderCode = readShader(fragmentShader);

        programId = OGLUtils.requireNotEquals(glCreateProgram(), 0, "Error creating shader program");

        glShaderSource(vertexShaderId, vertexShaderCode);
        glCompileShader(vertexShaderId);

        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error compiling vertex shader: " + glGetShaderInfoLog(vertexShaderId));
        }

        glShaderSource(fragmentShaderId, fragmentShaderCode);
        glCompileShader(fragmentShaderId);

        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error compiling fragment shader: " + glGetShaderInfoLog(fragmentShaderId));
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error linking shader program: " + glGetProgramInfoLog(programId));
        }

        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
            GL20.glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
            GL20.glDeleteShader(fragmentShaderId);
        }

        glValidateProgram(programId);

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error validating shader program: " + glGetProgramInfoLog(programId));
        }

    }

    @Override
    public void cleanup() throws OxygenException {
        unbind();
        if (programId != 0) glDeleteProgram(programId);
    }

    public <T> void setUniform(String name, T value) {
        try {
            int location = OGLUtils.requireNotEquals(glGetUniformLocation(programId, name), -1, "Uniform " + name + " not found");
            Uniform<T> uniform = new Uniform<>(name, location, value);
            uniform.setValue(value);
        }
        catch (OpenGLException e) {
            Oxygen.LOGGER.warn(e.getMessage());
        }
    }

    private String readShader(String name) throws OpenGLException {
        String result;
        try (FileInputStream inputStream = new FileInputStream("shaders/" + shaderName + "/" + name)) {
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
            result = scanner.hasNext() ? scanner.next() : "";
        }
        catch (IOException e) {
            throw new OpenGLException("Error reading shader file: " + name);
        }
        return result;
    }
}
