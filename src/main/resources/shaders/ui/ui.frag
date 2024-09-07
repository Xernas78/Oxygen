#version 450

in vec2 texCoordFrag;

uniform bool isTextured;
uniform sampler2D textureSampler;

uniform vec3 baseColor;

uniform bool visible;

out vec4 uFragColor;

void main() {

    if(!visible) {
        discard;
    }

    vec4 textured = texture(textureSampler, texCoordFrag);
    if(!isTextured) {
        textured = vec4(baseColor, 1);
    }
    uFragColor = textured;
}