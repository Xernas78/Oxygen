package dev.xernas.oxygen.engine.resource.loaders;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.ILoader;
import dev.xernas.oxygen.engine.resource.ResourceManager;
import dev.xernas.oxygen.engine.resource.formats.ObjFormat;
import dev.xernas.oxygen.exception.OxygenException;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader implements ILoader {

    private final ResourceManager resourceManager;

    public OBJLoader(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public ObjFormat loadFromResources(String resource, boolean textured) {
        String resourcePath = resourceManager.getModelsDir() + resource;
        List<String> lines = resourceManager.getLinesFromResource(resourcePath);
        if (lines == null) {
            Oxygen.LOGGER.fatal(new OxygenException("Resource not found: " + resourcePath));
            return null;
        }
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    Vector3f vertex = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(vertex);
                    break;
                case "vt":
                    Vector2f texCoord = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    texCoords.add(texCoord);
                    break;
                case "vn":
                    Vector3f normal = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normal);
                    break;
                case "f":
                    for(int i=1; i< tokens.length; i++){
                        processFace(tokens[i],faces);
                    }
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[i * 3] = vertex.x;
            verticesArray[i * 3 + 1] = vertex.y;
            verticesArray[i * 3 + 2] = vertex.z;
            i++;
        }
        float[] texCoordsArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face, indices, texCoords, normals, texCoordsArray, normalsArray);
        }

        int[] indicesArray = indices.stream().mapToInt(Integer::intValue).toArray();
        return new ObjFormat(verticesArray, indicesArray, normalsArray, texCoordsArray, faces.size(), textured);
    }

    private void processVertex(Vector3i face, List<Integer> indices, List<Vector2f> texCoords, List<Vector3f> normals, float[] texCoordsArray, float[] normalsArray) {
        indices.add(face.x);

        if (face.y >= 0) {
            Vector2f texCoordVec = texCoords.get(face.y);
            texCoordsArray[face.x * 2] = texCoordVec.x;
            texCoordsArray[face.x * 2 + 1] = 1 - texCoordVec.y;
        }

        if (face.z >= 0) {
            Vector3f normalVec = normals.get(face.z);
            normalsArray[face.x * 3] = normalVec.x;
            normalsArray[face.x * 3 + 1] = normalVec.y;
            normalsArray[face.x * 3 + 2] = normalVec.z;
        }

    }

    private void processFace(String token, List<Vector3i> faces) {
        String[] tokens = token.split("/");
        int length = tokens.length;
        int position = length > 0 ? Integer.parseInt(tokens[0]) - 1 : -1;
        int texCoord = length > 1 ? Integer.parseInt(tokens[1]) - 1 : -1;
        int normal = length > 2 ? Integer.parseInt(tokens[2]) - 1 : -1;
        Vector3i face = new Vector3i(position, texCoord, normal);
        faces.add(face);
    }

}
