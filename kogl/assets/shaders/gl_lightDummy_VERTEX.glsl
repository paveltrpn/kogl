layout (location = 0) in vec3 pos;

uniform mat4 view_matrix;
uniform mat4 model_matrix;

void main()
{
    gl_Position = view_matrix * model_matrix * vec4(pos, 1.0);
}
