#version 450 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 nrm;
layout (location = 2) in vec2 txc;

uniform mat4 view_matrix;
out vec2 TexCoord;

void main()
{
    gl_Position = view_matrix * vec4(pos, 1.0);
    TexCoord = txc;
}
