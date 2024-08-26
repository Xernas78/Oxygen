#version 450

out vec4 uFragColor;

in vec3 pixelPos;
in vec2 texCoordFrag;

in vec3 surfaceNormal;
in vec3 toLightDir[10];
in vec3 toCameraDir;

uniform bool isTextured;
uniform sampler2D textureSampler;

uniform vec3 baseColor;

uniform float ambientLight;
uniform bool illuminable;
uniform float reflectivity;
uniform float reflectionVisibility;
uniform vec3 lightColor[10];
uniform vec3 specularColor[10];
uniform float lightIntensity[10];

void main() {

    uFragColor = vec4(normalize(surfaceNormal), 1.0);

}