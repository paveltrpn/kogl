layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 nrm;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform vec3 color;

out vec3 worldPos;
out vec3 worldNrm;
out vec3 fragColor;

void main()
{
    mat4 itModel = transpose(inverse(model_matrix));
    vec4 updatedNrm = itModel * vec4(nrm, 0.0);

    vec4 wp = model_matrix * vec4(pos, 1.0);

    worldPos = wp.xyz;
    worldNrm = normalize(updatedNrm).xyz;
    fragColor = color;

    gl_Position = view_matrix * wp;
}
