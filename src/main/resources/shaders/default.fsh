#version 450

uniform sampler2D uSampler0;

in vec2 vUV;
in vec4 vColor;

out vec4 outColor;

void main() {
    outColor = texture(uSampler0, vUV) * vColor;
}
