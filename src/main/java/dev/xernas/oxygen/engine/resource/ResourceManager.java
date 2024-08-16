package dev.xernas.oxygen.engine.resource;

import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ResourceManager {

    private final Class<?> projectClass;

    public ResourceManager(Class<?> projectClass) {
        this.projectClass = projectClass;
    }

    public InputStream getResourceAsStream(String resourcePath) {
        try (InputStream is = projectClass.getClassLoader().getResourceAsStream(resourcePath)){
            return is;
        } catch (IOException e) {
            throw new RuntimeException("Error reading resource: " + resourcePath);
        }
    }

    public File getFile(String path) throws OxygenException {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) throw new OxygenException("Error creating file: " + file.getAbsolutePath());
            }
            catch (IOException e) {
                throw new OxygenException("Error creating file: " + file.getAbsolutePath());
            }
        }
        return file;
    }

    public void createFileFromResource(String resourcePath, String filePath) throws OxygenException {
        byte[] data = getBytesFromResource(resourcePath);
        writeBytesToFile(filePath, data);
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

    public void writeBytesToFile(String filePath, byte[] data) throws OxygenException {
        File file = getFile(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        catch (IOException e) {
            throw new OxygenException("Error writing data to file: " + file.getAbsolutePath());
        }
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

    public List<OGLShaderProgram> getShadersFromShadersDir() {
        List<OGLShaderProgram> shaderPrograms = new ArrayList<>();
        File shadersDir = new File("shaders/");
        File[] shaderDirs = shadersDir.listFiles();
        if (shaderDirs == null) return null;
        for (File shaderDir : shaderDirs) {
            if (shaderDir.isDirectory()) {
                File[] shaderFiles = shaderDir.listFiles();
                if (shaderFiles == null) return null;
                String shaderName = shaderDir.getName();
                shaderPrograms.add(new OGLShaderProgram(shaderName, !(shaderFiles.length >= 2)));
            }
        }
        return shaderPrograms;
    }

    public Properties getPropertiesFromFile(String path) throws OxygenException {
        File propertiesFile = getFile(path);
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties;
        }
        catch (IOException e) {
            throw new OxygenException("Error loading properties from file: " + propertiesFile.getAbsolutePath());
        }
    }

}
