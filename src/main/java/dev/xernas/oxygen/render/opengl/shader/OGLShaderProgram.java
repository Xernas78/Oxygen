package dev.xernas.oxygen.render.opengl.shader;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.exception.OpenGLException;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.IOGLObject;
import dev.xernas.oxygen.render.opengl.utils.OGLUtils;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class OGLShaderProgram implements IOGLObject {

    private int programId;

    private final ResourceManager resourceManager;
    private final String shaderName;
    private final String vertexShader;
    private final String fragmentShader;
    private final boolean useDefaultVertex;

    public OGLShaderProgram(ResourceManager resourceManager, String shaderName, boolean useDefaultVertex) {
        this.resourceManager = resourceManager;
        this.shaderName = shaderName;
        this.vertexShader = useDefaultVertex ? "default.vert" : shaderName + ".vert";
        this.fragmentShader = shaderName + ".frag";
        this.useDefaultVertex = useDefaultVertex;
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
        String vertexShaderCode = readShader(useDefaultVertex ? "default" : shaderName, vertexShader);
        String fragmentShaderCode = readShader(shaderName, fragmentShader);

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

    public <T> void setUniform(String name, T value) throws OxygenException {
        int location = glGetUniformLocation(programId, name);
        if (location == -1) return;
        Uniform<T> uniform = new Uniform<>(name, location);
        if (value == null) {
            throw new OxygenException("Trying to set uniform with null value: " + name);
        }
        uniform.setValue(value);
    }

    private String readShader(String shaderName, String name) throws OxygenException {
        Path filePath = resourceManager.getResourcePath(resourceManager.getShadersDir() + shaderName + "/" + name);
        if (!Files.exists(filePath)) {
            throw new OpenGLException("Resource not found: " + filePath);
        }
        try (InputStream is = Files.newInputStream(filePath)) {
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8);
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new OpenGLException("Error reading shader file: " + e.getMessage());
        }
    }

    public String getShaderName() {
        return shaderName;
    }
}
