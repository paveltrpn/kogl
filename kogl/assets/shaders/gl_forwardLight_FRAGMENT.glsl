const int MAX_LIGHTS = 16;

struct OmniLight {
    vec3 pos;
    vec3 color;
    float intensity;
    float radius;
};

uniform OmniLight lights[MAX_LIGHTS];
uniform int lightsCount;

in vec3 worldPos;
in vec3 worldNrm;
in vec3 fragColor;

out vec4 outColor;

void main()
{
    float ambient = 0.08;
    vec3 n = normalize(worldNrm);
    vec3 sum = vec3(0.0, 0.0, 0.0);

    for (int i = 0; i < lightsCount; ++i) {
        vec3 L = lights[i].pos - worldPos;
        float d = length(L);

        float att = clamp(1.0 - d / lights[i].radius, 0.0, 1.0);
        att *= att;

        float diffuse = max(dot(n, normalize(L)), 0.0);

        sum += lights[i].color * lights[i].intensity * att * diffuse;
    }

    outColor = vec4(fragColor * (ambient + sum), 1.0);
}
