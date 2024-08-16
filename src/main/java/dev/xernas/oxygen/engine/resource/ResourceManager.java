package dev.xernas.oxygen.engine.resource;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ResourceManager {

    private final Class<?> projectClass;
    private final String shadersDir;
    private final String modelsDir;
    private final String texturesDir;

    public ResourceManager(Class<?> projectClass, String shadersDir, String modelsDir, String texturesDir) {
        this.projectClass = projectClass;
        this.shadersDir = shadersDir;
        this.modelsDir = modelsDir;
        this.texturesDir = texturesDir;
    }

    public ResourceManager(Class<?> projectClass) {
        this(projectClass, "", "", "");
    }

    public InputStream getResourceAsStream(String resourcePath) {
        try (InputStream is = projectClass.getClassLoader().getResourceAsStream(resourcePath)){
            return is;
        } catch (IOException e) {
            throw new RuntimeException("Error reading resource: " + resourcePath);
        }
    }

    public File getFileFromResource(String path) throws OxygenException {
        URL resource = projectClass.getClassLoader().getResource(path);
        if (resource == null) throw new OxygenException("Resource not found: " + path);
        return new File(resource.getFile());
    }

    public byte[] getBytesFromResource(String resourcePath) throws OxygenException {
        try (InputStream is = projectClass.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new OxygenException("Resource not found: " + resourcePath);
            byte[] data = new byte[is.available()];
            is.read(data);
            return data;
        }
        catch (IOException e) {
            throw new OxygenException("Error reading file data from resource: " + resourcePath);
        }
    }

    public String getFileResourceAbsolutePath(String resourcePath) {
        URL resource = projectClass.getClassLoader().getResource(resourcePath);
        if (resource == null) return null;
        return new File(Objects.requireNonNull(resource).getFile()).getAbsolutePath();
    }

    public List<String> getLinesFromResource(String resourcePath) {
        try (InputStream is = projectClass.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                return br.lines().toList();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading lines from resource: " + resourcePath);
        }
    }

    public List<OGLShaderProgram> getShadersFromShadersDir() throws OxygenException {
        List<OGLShaderProgram> shaderPrograms = new ArrayList<>();
        File shadersDir = getFileFromResource(this.shadersDir);
        File[] shaderDirs = shadersDir.listFiles();
        if (shaderDirs == null) return new ArrayList<>();
        for (File shaderDir : shaderDirs) {
            if (shaderDir.isDirectory()) {
                File[] shaderFiles = shaderDir.listFiles();
                if (shaderFiles == null) continue;
                String shaderName = shaderDir.getName();
                String firstShader = shaderFiles[0].getName();
                if (!firstShader.endsWith(".vert") && !firstShader.endsWith(".frag")) {
                    continue;
                }
                shaderPrograms.add(new OGLShaderProgram(this, shaderName, !(shaderFiles.length >= 2)));
            }
        }
        return shaderPrograms;
    }

    public Class<?> getProjectClass() {
        return projectClass;
    }

    public String getShadersDir() {
        return shadersDir;
    }

    public String getModelsDir() {
        return modelsDir;
    }

    public String getTexturesDir() {
        return texturesDir;
    }
}
