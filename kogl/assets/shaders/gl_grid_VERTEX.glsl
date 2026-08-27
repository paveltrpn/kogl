uniform mat4 view_matrix;
uniform mat4 model_matrix;

uniform float scale;
uniform float yOffset;

out vec3 vWorldPos;

vec4 vertecies[6] = vec4[](
        vec4(0.5, 0.0, -0.5, 1.0), vec4(0.5, 0.0, 0.5, 1.0), vec4(-0.5, 0.0, 0.5, 1.0),
        vec4(-0.5, 0.0, 0.5, 1.0), vec4(-0.5, 0.0, -0.5, 1.0), vec4(0.5, 0.0, -0.5, 1.0));

void main() {
    mat4 planeScale = mat4(1.0);
    planeScale[0][0] = scale;
    planeScale[2][2] = scale;

    mat4 planeYOffset = mat4(1.0);
    planeYOffset[3][1] = yOffset;

    vec4 worldPos = planeScale * planeYOffset * vertecies[gl_VertexID];

    vWorldPos = worldPos.xyz;
    gl_Position = view_matrix * model_matrix * worldPos;
}



