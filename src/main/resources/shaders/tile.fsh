#version 330

uniform vec4 uColor;
uniform sampler2D uIconsSampler;
uniform sampler2D uPaletteSampler;
uniform int uTile;
uniform bvec2 uMirror;
uniform int uTileColors;
uniform bool uTextured;

in vec2 vUV;

vec3 getRGBFromPalette(int index) {
    return texture(uPaletteSampler, vec2((index % 16) / 16.0f, index / 16 / 16.0f));
}

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

    int colorIndex = (uTileColors >> (int(texture(uIconsSampler, fUV) / 64.0 * 255.0) * 8)) & 0xFF;

    if (colorIndex > 216) {
        discard;
    }

    return uColor * vec4(getRGBFromPalette(colorIndex), 1.0);
}
