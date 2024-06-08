#version 330

uniform vec4 uColor;
uniform sampler2D uIconsSampler;
uniform sampler2D uPaletteSampler;
uniform int uTile;
uniform bvec2 uMirror;
uniform int uTileColors;
uniform bool uTextured;

in vec2 vUV;

#include "palette.glsl"

vec4 main() {
    if (!uTextured) {
        return uColor;
    }

    vec2 fUV = vUV;

    if (uMirror.x) {
        fUV.x = 1 - fUV.x;
    }

    if (uMirror.y) {
        fUV.y = 1 - fUV.y;
    }

    int tX = uTile % 32;
    int tY = uTile / 32;

    fUV += vec2(tX, tY);
    fUV *= 8;
    fUV /= textureSize(uIconsSampler, 0);

    int colorIndex = calculatePaletteIndex(uIconsSampler, fUV, uTileColors);

    if (colorIndex > 216) {
        discard;
    }

    return uColor * vec4(getRGBFromPalette(uPaletteSampler, colorIndex), 1.0);
}
