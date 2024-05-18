#version 330

uniform vec4 uColor;
uniform ivec4 uRectangle;
uniform int uRadius;

in vec2 vUV;

vec4 main() {
    // Credits: https://github.com/zandgall/minicraft-plus-revived/blob/lwjgl/src/client/resources/assets/shaders/lighting.shader
    vec2 p = (vUV - vec2(0.5)) * (uRectangle.zw - uRectangle.xy);
    float dist = p.x * p.x + p.y * p.y;
    float br = 1 - dist / (uRadius * uRadius);
    return vec4(1, 0, 0, br);
}
