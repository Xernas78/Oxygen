package dev.xernas.oxygen.engine.model;

import dev.xernas.oxygen.engine.material.Material;

public class Models {

    private static Model generateCube() {
        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f
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

    private static Model generatePlane(int vertexCount, int size) {
        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
        int vertexPointer = 0;
        for(int i=0;i<vertexCount;i++){
            for(int j=0;j<vertexCount;j++){
                vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * size;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * size;
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

    public static Model getCube() {
        return generateCube().copy();
    }

    public static Model getPlane(int vertexCount, int size) {
        return generatePlane(vertexCount, size).copy();
    }
}
