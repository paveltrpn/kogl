uniform mat4 view_matrix;
uniform mat4 model_matrix;

uniform float scale;
uniform float yOffset;

out vec3 vWorldPos;

vec4 vertecies[6] = vec4[](
        vec4(1.0, 0.0, -1.0, 1.0), vec4(1.0, 0.0, 1.0, 1.0), vec4(-1.0, 0.0, 1.0, 1.0),
        vec4(-1.0, 0.0, 1.0, 1.0), vec4(-1.0, 0.0, -1.0, 1.0), vec4(1.0, 0.0, -1.0, 1.0));

void main() {
    mat4 planeScale = mat4(0.0);
    planeScale[0][0] = scale;
    planeScale[1][1] = 1.0;
    planeScale[2][2] = scale;
    planeScale[3][3] = 1.0;

    mat4 planeYOffset = mat4(0.0);
    planeYOffset[3][0] = 0.0f;
    planeYOffset[3][1] = yOffset;
    planeYOffset[3][2] = 0.0f;
    planeYOffset[3][3] = 1.0f;

    planeYOffset[0][0] = 1.0;
    planeYOffset[1][1] = 1.0;
    planeYOffset[2][2] = 1.0;
    planeYOffset[3][3] = 1.0;

    vec4 worldPos = planeScale * planeYOffset * vertecies[gl_VertexID];

    vWorldPos = -worldPos.xyz;
    gl_Position = view_matrix * model_matrix * worldPos;
}



