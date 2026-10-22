layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec4 col;

uniform vec2 scale;
uniform vec2 translate;

out vec2 fragUv;
out vec4 fragColor;

void main()
{
    gl_Position = vec4(pos * scale + translate, 0.0, 1.0);
    fragUv = uv;
    fragColor = col;
}
