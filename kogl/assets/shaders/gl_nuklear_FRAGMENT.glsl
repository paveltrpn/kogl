uniform sampler2D fontTex;

in vec2 fragUv;
in vec4 fragColor;
out vec4 outColor;

void main()
{
    outColor = fragColor * texture(fontTex, fragUv);
}
