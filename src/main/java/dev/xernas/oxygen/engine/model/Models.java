package dev.xernas.oxygen.engine.model;

import dev.xernas.oxygen.engine.material.Material;

public class Models {

    private static final Model CUBE;

    static {
        CUBE = new Model(new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f
        }, new int[] {
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
        }, new float[] {
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
        }, new float[] {
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        }, Material.DEFAULT);
    }

    public static Model getCube() {
        return CUBE.copy();
    }

}
