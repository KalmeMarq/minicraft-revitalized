vec3 getRGBFromPalette(sampler2D paletteSampler, int index) {
    return texture(paletteSampler, vec2((index % 16) / 16.0, index / 16 / 16.0));
}

int calculatePaletteIndex(sampler2D iconsSampler, vec2 uv, int color) {
  return (color >> (int(texture(iconsSampler, uv) / 64.0 * 255.0) * 8)) & 0xFF;
}
