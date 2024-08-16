#version 450

in vec3 pixelPos;
in vec2 texCoordFrag;

in vec3 surfaceNormal;
in vec3 toLightDir[10];

uniform bool isTextured;
uniform sampler2D textureSampler;

uniform vec3 lightColor[10];

uniform float lightIntensity[10];

out vec4 uFragColor;

void main()
{
    vec3 normal = normalize(surfaceNormal);

    vec3 totalDiffuse = vec3(0.0);

    for (int i = 0; i < 10; i++) {
        vec3 toLightDirNormalized = normalize(toLightDir[i]);
        float brightness = max(dot(normal, toLightDirNormalized), 0.0);
        totalDiffuse += brightness * lightColor[i] * lightIntensity[i];
    }

    totalDiffuse = max(totalDiffuse, 0.2);

    vec4 textured = texture(textureSampler, texCoordFrag);
    if(!isTextured) {
        textured = vec4(1, 1, 1, 1);
    }
    uFragColor = vec4(totalDiffuse, 1.0) * textured;
}