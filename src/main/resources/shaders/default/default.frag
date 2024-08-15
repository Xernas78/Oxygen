#version 450

in vec3 pixelPos;
in vec2 texCoordFrag;

uniform bool isTextured;
uniform sampler2D textureSampler;

out vec4 uFragColor;

void main()
{
    if (isTextured)
    {
        uFragColor = texture(textureSampler, texCoordFrag);
    } else {
        uFragColor = vec4(pixelPos, 1);
    }
}