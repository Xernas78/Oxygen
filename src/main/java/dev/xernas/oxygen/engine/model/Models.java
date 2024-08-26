package dev.xernas.oxygen.engine.model;

import dev.xernas.oxygen.engine.material.Material;

public class Models {

    private static Model generateCuboid(int width, int height, int depth) {
        float[] vertices = new float[] {
                -width, height, depth,
                -width, -height, depth,
                width, -height, depth,
                width, height, depth,
                -width, height, -depth,
                -width, -height, -depth,
                width, -height, -depth,
                width, height, -depth
        };
        int[] indices = new int[] {
                0, 1, 3,
                3, 1, 2,
                3, 2, 7,
                7, 2, 6,
                7, 6, 4,
                4, 6, 5,
                4, 5, 0,
                0, 5, 1,
                4, 0, 7,
                7, 0, 3,
                1, 5, 2,
                2, 5, 6
        };
        float[] normals = new float[] {
                // Front
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                // Back
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                // Right
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                // Left
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                // Top
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                // Bottom
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0
        };

        float[] texCoords = new float[] {
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        return new Model(vertices, indices, normals, texCoords, Material.DEFAULT);
    }

    private static Model generatePlane(int width, int height) {
//        float[] vertices = new float[] {
//                -width, 0, height,
//                -width, 0, -height,
//                width, 0, -height,
//                width, 0, height
//        };
//        int[] indices = new int[] {
//                0, 1, 3,
//                3, 1, 2
//        };
//        float[] normals = new float[] {
//                0, 1, 0,
//                0, 1, 0,
//                0, 1, 0,
//                0, 1, 0
//        };
//        float[] texCoords = new float[] {
//                0, 0,
//                0, 1,
//                1, 1,
//                1, 0
//        };
//        return new Model(vertices, indices, normals, texCoords, Material.DEFAULT);
        return generateSubdividedPlane(2, width, height);
    }

    private static Model generateSubdividedPlane(int vertexCount, int width, int height) {
        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
        int vertexPointer = 0;
        for(int i=0;i<vertexCount;i++){
            for(int j=0;j<vertexCount;j++){
                vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * width;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * height;
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
                textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<vertexCount-1;gz++){
            for(int gx=0;gx<vertexCount-1;gx++){
                int topLeft = (gz*vertexCount)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*vertexCount)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return new Model(vertices, indices, normals, textureCoords, Material.DEFAULT);
    }

    private static Model generateSphere(int resolution, int radius) {
        int vertexCount = (resolution + 1) * (resolution + 1);
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        int[] indices = new int[6 * resolution * resolution];

        int vertexPointer = 0;
        for (int lat = 0; lat <= resolution; lat++) {
            float theta = (float) (lat * Math.PI / resolution);  // Latitude angle
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int lon = 0; lon <= resolution; lon++) {
                float phi = (float) (lon * 2 * Math.PI / resolution);  // Longitude angle
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;
                float u = 1 - (lon / (float) resolution);
                float v = 1 - (lat / (float) resolution);

                vertices[vertexPointer * 3] = radius * x;
                vertices[vertexPointer * 3 + 1] = radius * y;
                vertices[vertexPointer * 3 + 2] = radius * z;

                normals[vertexPointer * 3] = x;
                normals[vertexPointer * 3 + 1] = y;
                normals[vertexPointer * 3 + 2] = z;

                texCoords[vertexPointer * 2] = u;
                texCoords[vertexPointer * 2 + 1] = v;

                vertexPointer++;
            }
        }

        int indexPointer = 0;
        for (int lat = 0; lat < resolution; lat++) {
            for (int lon = 0; lon < resolution; lon++) {
                int first = (lat * (resolution + 1)) + lon;
                int second = first + resolution + 1;

                indices[indexPointer++] = first;
                indices[indexPointer++] = second;
                indices[indexPointer++] = first + 1;

                indices[indexPointer++] = second;
                indices[indexPointer++] = second + 1;
                indices[indexPointer++] = first + 1;
            }
        }

        return new Model(vertices, indices, normals, texCoords, Material.DEFAULT);
    }

    public static Model getCuboid(int width, int height, int depth) {
        return generateCuboid(width, height, depth).copy();
    }

    public static Model getCube(int size) {
        return generateCuboid(size, size, size).copy();
    }

    public static Model getSubdividedPlane(int size, int vertexCount) {
        if (vertexCount < 2) {
            return generatePlane(size, size).copy();
        }
        return generateSubdividedPlane(vertexCount, size, size).copy();
    }

    public static Model getPlane(int width, int height) {
        return generatePlane(width, height).copy();
    }

    public static Model getPlane(int size) {
        return getPlane(size, size);
    }

    public static Model getSphere(int resolution, int radius) {
        return generateSphere(resolution, radius).copy();
    }
}
