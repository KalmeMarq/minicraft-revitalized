#version 330

uniform vec4 uColor;
uniform sampler2D uScreenSampler;
uniform sampler2D uLightSampler;
uniform sampler2D uDitherSampler;
uniform ivec2 uAdjust;
uniform float uDarknessOverlayAlpha;

in vec2 vUV;

// https://github.com/zandgall/minicraft-plus-revived/blob/lwjgl2/src/client/resources/assets/shaders/overlay.fs
vec4 main() {
    vec2 nUV = vUV * textureSize(uScreenSampler, 0) / textureSize(uDitherSampler, 0);
    nUV += uAdjust / textureSize(uDitherSampler, 0);
    vec4 color = texture(uScreenSampler, vUV);
    float overlayAmount = texture(uLightSampler, vUV).x;
    float ditherSample = texture(uDitherSampler, nUV).x;
    if (overlayAmount <= ditherSample) {
        float dark = (255 * (1 - uDarknessOverlayAlpha + 0.1)) / 255.0;
        color.xyz *= vec3(dark, dark, dark);
        return color;
    }
    color.xyz += 0.05;
    color.w = 1.0;
    return color;
}
