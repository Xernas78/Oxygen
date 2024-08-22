package dev.xernas.oxygen.engine.resource;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.opengl.shader.OGLShaderProgram;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class ResourceManager {

    private static final List<FileSystem> fileSystems = new ArrayList<>();

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

    public Path getResourcePath(String path) throws OxygenException {
        URL resource = projectClass.getClassLoader().getResource(path);
        if (resource == null) {
            throw new OxygenException("Resource not found: " + path);
        }
        try {
            URI resourceUri = resource.toURI();
            if (resourceUri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.newFileSystem(resourceUri, Collections.emptyMap());
                    fileSystems.add(fileSystem);
                } catch (FileSystemAlreadyExistsException e) {
                    fileSystem = FileSystems.getFileSystem(resourceUri);
                }
                return fileSystem.getPath(path);
            }
            return Paths.get(resourceUri);
        } catch (URISyntaxException | IOException e) {
            throw new OxygenException("Error getting resource path: " + path);
        }
    }

    public List<Path> listResources(Path resourceDirectoryPath) throws OxygenException {
        try {
            return list(resourceDirectoryPath);
        } catch (IOException e) {
            throw new OxygenException("Error listing resources in directory: " + resourceDirectoryPath);
        }
    }

    private List<Path> list(Path path) throws IOException {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                paths.add(entry);
            }
        }
        return paths;
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

    public Path getResourceAbsolutePath(String resourcePath) {
        try {
            return getResourcePath(resourcePath).toAbsolutePath();
        } catch (OxygenException e) {
            return null;
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

    public List<OGLShaderProgram> getShadersFromShadersDir() throws OxygenException {
        List<OGLShaderProgram> shaderPrograms = new ArrayList<>();
        Path shadersDirPath = getResourcePath(this.shadersDir);
        List<Path> shaderDirs = listResources(shadersDirPath);
        if (shaderDirs == null || shaderDirs.isEmpty()) return new ArrayList<>();
        for (Path shaderDir : shaderDirs) {
            if (Files.isDirectory(shaderDir)) {
                List<Path> shaderFiles = listResources(shaderDir);
                if (shaderFiles == null || shaderFiles.isEmpty()) continue;
                String shaderName = shaderDir.getName(shaderDir.getNameCount() - 1).toString();
                String firstShader = shaderFiles.get(0).getName(shaderFiles.get(0).getNameCount() - 1).toString();
                if (!firstShader.endsWith(".vert") && !firstShader.endsWith(".frag")) {
                    continue;
                }
                shaderPrograms.add(new OGLShaderProgram(this, shaderName, !(shaderFiles.size() >= 2)));
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

    public static void closeFileSystems() {
        for (FileSystem fileSystem : fileSystems) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                Oxygen.LOGGER.warn("Error closing file system: " + e.getMessage());
            }
        }
    }
}
