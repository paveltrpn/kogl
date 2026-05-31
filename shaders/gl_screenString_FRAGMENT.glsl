#version 450 core

in vec2 TexCoord;
in vec4 Color;
out vec4 FragColor;

uniform sampler2D ourTexture;

void main() {
    FragColor = texture(ourTexture, TexCoord) * Color;
};
