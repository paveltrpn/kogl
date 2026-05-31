#version 450 core

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexNrm;
layout (location = 2) in vec2 vertexTxc;

// GL_ARB_gpu_shader_fp64 double precision values
uniform dmat4 viewMatrix;
uniform dvec3 eyePosition;

uniform vec3 diffuseColor;
uniform int lightsCount;

out vec3 EyePosition;
out vec3 DiffuseColor;
out vec2 TexCoord;
out vec3 Normal;
out vec3 FragmentPosition;

// For normal mapping
out vec2 FragmentTexcrd;  

// Current scene lights count
out flat uint LightsCount;

void main()
{
    // Explicitly converts to 32 bit precision, by now...
    gl_Position = mat4(viewMatrix) * vec4(vertexPos, 1.0);
    EyePosition = vec3(eyePosition.xyz);

    FragmentPosition = vertexPos;
    FragmentTexcrd = vertexTxc;
    DiffuseColor = diffuseColor;
    TexCoord = vertexTxc;
    Normal = vertexNrm;
    LightsCount = lightsCount;
}
