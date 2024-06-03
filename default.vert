#version 450

layout(location = 0) in vec3 pos;

out vec3 pixelPos;

void main()
{
    gl_Position = vec4(pos, 1);
    pixelPos = vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
}