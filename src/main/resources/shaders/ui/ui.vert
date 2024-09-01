#version 450

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoordIn;

uniform mat4 transformMatrix;
uniform mat4 orthoMatrix;

out vec2 texCoordFrag;


void main() {
    gl_Position = orthoMatrix * transformMatrix * vec4(pos.xy, 0, 1);
    texCoordFrag = texCoordIn;
}
