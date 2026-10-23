out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D depthTexture;
uniform sampler2D noiseTexture;

// Corresponding to _KERNEL_SIZE
uniform vec3 samples[128];

uniform vec2 noiseScale;

uniform float nearPlane;
uniform float farPlane;
uniform float fov;
uniform float aspect;
uniform float radius;
uniform float bias;
uniform int kernelSize;

float linearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (2.0 * nearPlane * farPlane) / (farPlane + nearPlane - z * (farPlane - nearPlane));
}

void main()
{
    float depth = texture(depthTexture, TexCoords).r;

    if (depth == 1.0) {
        FragColor = vec4(1.0);
        return;
    }

    float tanHalfFov = tan(radians(fov) * 0.5);

    float linearDepth = linearizeDepth(depth);

    vec2 ndc = TexCoords * 2.0 - 1.0;

    //      vec4 clipSpacePos = projectionMatrix * vec4(sample, 1.0); // from view to clip-space
    //      vec3 ndcSpacePos = clipSpacePos.xyz /= clipSpacePos.w; // perspective divide
    //      vec2 windowSpacePos = ((ndcSpacePos.xy + 1.0) / 2.0) * vec2(screenWidth, screenHeight);
    //
    //      if ((windowSpacePos.y > 0) && (windowSpacePos.y < screenHeight))
    //          if ((windowSpacePos.x > 0) && (windowSpacePos.x < screenWidth))

    vec3 viewPos = vec3(
            ndc.x * linearDepth * tanHalfFov * aspect,
            ndc.y * linearDepth * tanHalfFov,
            -linearDepth
    );

    vec3 normal = normalize(cross(dFdx(viewPos), dFdy(viewPos)));

    vec3 randomVec = normalize(texture(noiseTexture, TexCoords * noiseScale).xyz);

    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;

    for (int i = 0; i < kernelSize; ++i) {
        vec3 samplePos = viewPos + TBN * samples[i] * radius;

        float sampleLinearDepth = -samplePos.z;

        vec2 sampleNdc = vec2(
                samplePos.x / (tanHalfFov * aspect * sampleLinearDepth),
                samplePos.y / (tanHalfFov * sampleLinearDepth)
        );

        vec2 sampleUv = sampleNdc * 0.5 + 0.5;

        float geometryDepth = texture(depthTexture, sampleUv).r;
        float geometryViewZ = -linearizeDepth(geometryDepth);

        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(viewPos.z - geometryViewZ));

        if (geometryViewZ >= samplePos.z + bias) {
            occlusion += 1.0 * rangeCheck;
        }
    }

    occlusion = 1.0 - (occlusion / float(kernelSize));

    FragColor = vec4(vec3(occlusion), 1.0);
}
