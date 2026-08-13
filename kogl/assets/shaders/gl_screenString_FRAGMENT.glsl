#version 450 core

in vec2 TexCoord;
in vec4 Color;
out vec4 FragColor;

uniform sampler2D font;

void main() {
    FragColor = texture(font, TexCoord) * Color;
    // FragColor = Color;
}
