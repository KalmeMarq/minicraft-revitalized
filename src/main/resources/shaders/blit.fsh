#version 330

uniform sampler2D uSampler0;
uniform float uGamma;

in vec2 vUV;

vec4 main() {
    vec4 color = texture(uSampler0, vUV);
    vec3 washedColor = pow(color.rgb, vec3(1.0 / uGamma));
    return vec4(washedColor, 1.0);
}
