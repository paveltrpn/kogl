#version 450 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 nrm;
uniform mat4 view_matrix;
uniform vec3 color;
uniform vec3 lightpos;
uniform vec3 lightcolor;

out vec3 newColor;
out vec3 vLighting;

void main()
{
    vec3 ambientLight = vec3(0.3, 0.3, 0.3);
    vec3 directionalVector = normalize(lightpos);
    float directional = max(dot(nrm.xyz, directionalVector), 0.0);
    vLighting = ambientLight + (lightcolor * directional);
    gl_Position = view_matrix * vec4(pos, 1.0);
    newColor = color;
}
