#version 450

in vec3 pixelPos;
out vec4 uFragColor;

void main()
{
    uFragColor = vec4(pixelPos, 1);
}