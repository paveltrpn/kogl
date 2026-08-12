#version 450 core

layout (location = 0) in vec3 pos;

out vec4 outColor;

uniform mat4 view_matrix;
uniform vec4 color;

void main() {
    vec4 vertexColor = vec4(1.0, 0.0, 0.0, 1.0);
    gl_Position = view_matrix * vec4(pos, 1.0);

    //gl_Position = vec4(pos, 1.0);

    // outColor = color;
    outColor = vertexColor;
}
