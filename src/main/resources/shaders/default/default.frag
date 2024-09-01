#version 450

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

uniform bool visible;

out vec4 uFragColor;

void main()
{

    if(!visible) {
        discard;
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 toCamera = normalize(toCameraDir);

    vec3 totalLighting = vec3(0.0);
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 10; i++) {
        vec3 toLight = normalize(toLightDir[i]);
        float brightness = max(dot(normal, toLight), 0.0);

        vec3 fromLight = -toLight;
        vec3 reflectedDir = reflect(fromLight, normal);
        float specularFactor = pow(max(dot(reflectedDir, toCamera), 0.0), reflectionVisibility);

        vec3 specular = specularFactor * specularColor[i] * reflectivity;
        vec3 diffuse = brightness * lightColor[i] * lightIntensity[i];

        totalDiffuse += diffuse;
        totalSpecular += specular;
    }

    totalLighting = max(totalDiffuse + totalSpecular, ambientLight);

    vec4 textured = texture(textureSampler, texCoordFrag);
    if(!isTextured) {
        textured = vec4(baseColor, 1);
    }
    if(!illuminable) {
        totalLighting = vec3(1);
    }
    uFragColor = vec4(totalLighting, 1.0) * textured;
}