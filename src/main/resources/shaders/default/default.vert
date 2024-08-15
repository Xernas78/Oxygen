#version 450

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoordIn;

uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 pixelPos;
out vec2 texCoordFrag;

void main()
{
    gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(pos, 1);
    pixelPos = vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
    texCoordFrag = texCoordIn;
}