#version 450 core

in vec2 TexCoord;
in vec4 Color;
out vec4 FragColor;

layout(location = 2) uniform sampler2D font;
layout(location = 3) uniform vec4 color;
layout(location = 4) uniform int enableTexture;

void main() {
    if (enableTexture == 1) {
        FragColor = texture(font, TexCoord) * color;
    } else {
        FragColor = color;
    }
}
