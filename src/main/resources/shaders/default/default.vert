#version 450

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoordIn;
layout(location = 2) in vec3 normal;

uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPos[10];

uniform int numTextureTiles;

out vec3 pixelPos;
out vec2 texCoordFrag;

out vec3 surfaceNormal;
out vec3 toLightDir[10];
out vec3 toCameraDir;

void main()
{
    vec4 worldPos = transformMatrix * vec4(pos, 1);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    pixelPos = pos;
    texCoordFrag = texCoordIn * numTextureTiles;

    surfaceNormal = (transformMatrix * vec4(normal, 0.0)).xyz;
    for (int i = 0; i < 10; i++)
    {
        toLightDir[i] = lightPos[i] - worldPos.xyz;
    }
    toCameraDir = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPos.xyz;
}