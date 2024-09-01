package dev.xernas.oxygen.engine.noise;

import java.util.Random;

public class PerlinNoise {
    private double seed;
    private long default_size;
    private int[] p;

    public PerlinNoise() {
        this.seed = new Random().nextGaussian() * 255;
    }

    public void setSeed(double seed) {
        this.seed = seed;
    }

    public double getSeed() {
        return this.seed;
    }

    public double noise(double x, double y, double z, int size) {
        double value = 0.0;
        double initialSize = size;

        while (size >= 1) {
            value += smoothNoise((x / size), (y / size), (z / size)) * size;
            size /= (int) 2.0;
        }

        return value / initialSize;
    }

    public double noise(double x, double y, double z) {
        double value = 0.0;
        double size = default_size;
        double initialSize = size;

        while (size >= 1) {
            value += smoothNoise((x / size), (y / size), (z / size)) * size;
            size /= 2.0;
        }

        return value / initialSize;
    }

    public double noise(double x, double y) {
        double value = 0.0;
        double size = default_size;
        double initialSize = size;

        while (size >= 1) {
            value += smoothNoise((x / size), (y / size), (0f / size)) * size;
            size /= 2.0;
        }

        return value / initialSize;
    }

    public double noise(double x) {
        double value = 0.0;
        double size = default_size;
        double initialSize = size;

        while (size >= 1) {
            value += smoothNoise((x / size), (0f / size), (0f / size)) * size;
            size /= 2.0;
        }

        return value / initialSize;
    }

    public double smoothNoise(double x, double y, double z) {
        // Offset each coordinate by the seed value
        x += this.seed;
        y += this.seed;
        x += this.seed;

        int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
        int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x); // FIND RELATIVE X,Y,Z
        y -= Math.floor(y); // OF POINT IN CUBE.
        z -= Math.floor(z);

        double u = fade(x); // COMPUTE FADE CURVES
        double v = fade(y); // FOR EACH OF X,Y,Z.
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z; // HASH COORDINATES OF
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p[AA], 		x, 		y, 		z		), 	// AND ADD
                                grad(p[BA],		x - 1, 	y, 		z		)), // BLENDED
                        lerp(u, grad(p[AB], 	x, 		y - 1, 	z		), 	// RESULTS
                                grad(p[BB], 	x - 1, 	y - 1, 	z		))),// FROM 8
                lerp(v, lerp(u, grad(p[AA + 1], x, 		y, 		z - 1	), 	// CORNERS
                                grad(p[BA + 1], x - 1, 	y, 		z - 1	)), // OF CUBE
                        lerp(u, grad(p[AB + 1], x, 		y - 1,	z - 1	),
                                grad(p[BB + 1], x - 1, 	y - 1, 	z - 1	))));
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
        double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
