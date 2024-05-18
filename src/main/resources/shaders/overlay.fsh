#version 330

uniform vec4 uColor;
uniform sampler2D uScreenSampler;
uniform sampler2D uLightSampler;
uniform sampler2D uDitherSampler;
uniform vec2 uAdjust;

in vec2 vUV;

// https://github.com/zandgall/minicraft-plus-revived/blob/lwjgl/src/client/resources/assets/shaders/overlay.shader
vec4 main() {
    vec2 nUV = vUV * textureSize(uScreenSampler, 0) / textureSize(uDitherSampler, 0);
    nUV += uAdjust / textureSize(uDitherSampler, 0);
    vec4 color = texture(uScreenSampler, vUV);
    float overlayAmount = texture(uLightSampler, vUV).x;
    float ditherSample = texture(uDitherSampler, nUV).x;
    if (overlayAmount <= ditherSample) {
//        color.xyz = color.xyz * vec3(0.001, 0.001, 0.001);
        float dark = 9 / 255.0;
        return vec4(dark, dark, dark, 1);
    }
    color.xyz += vec3(20.0 / 256.0);
    color.w = 1.0;
    return color;
}
