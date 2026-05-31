#version 450 core
layout (location = 0) in vec3 pos;
out vec3 outColor;
uniform mat4 view_matrix;
uniform vec3 color;
void main() {
   outColor = color;
   gl_Position = view_matrix * vec4(pos, 1.0);
};
