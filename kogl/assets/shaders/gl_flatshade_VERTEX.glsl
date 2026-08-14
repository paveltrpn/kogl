layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 nrm;

uniform mat4 view_matrix;
uniform mat4 drawable_matrix;
uniform vec3 color;

out vec3 newColor;
out vec3 vLighting;

void main()
{
    vec3 lightpos = vec3(1.0, 1.0, 0.0);
    vec3 lightcolor = vec3(1.0, 1.0, 1.0);
    vec3 ambientLight = vec3(0.3, 0.3, 0.3);

    vec3 directionalVector = normalize(lightpos);

    mat4 itModel = transpose(inverse(drawable_matrix));
    vec4 updatedNrm = itModel * vec4(nrm, 0.0);

    float directional = max(dot(normalize(updatedNrm).xyz, directionalVector), 0.0);

    vLighting = ambientLight + (lightcolor * directional);

    mat4 mvp = view_matrix * drawable_matrix;
    gl_Position = mvp * vec4(pos, 1.0);

    newColor = color;
}
