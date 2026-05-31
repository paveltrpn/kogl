#version 450 core

in vec3 newColor;
in vec3 vLighting;

out vec4 outColor;

void main()
{
    outColor = vec4(newColor.rgb*vLighting, 1.0f);
}
