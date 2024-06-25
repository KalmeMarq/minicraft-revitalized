#version 450

uniform sampler2D uSampler0;
uniform float uGamma;

in vec2 vUV;

out vec4 outColor;

void main() {
    vec4 color = texture(uSampler0, vUV);
    vec3 washedColor = pow(color.rgb, vec3(1.0 / uGamma));
    outColor = vec4(washedColor, 1.0);
}
