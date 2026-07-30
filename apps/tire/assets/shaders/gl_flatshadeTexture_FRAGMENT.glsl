#version 450 core

in vec3 newColor;
in vec3 vLighting;
in vec2 texCoord;

out vec4 outColor;

uniform sampler2D albedoTexture;

void main()
{
    vec4 textureSample = texture(albedoTexture, texCoord);
    vec4 shadeColor = vec4(newColor.rgb*vLighting, 1.0f);
    outColor = textureSample*shadeColor;
}
