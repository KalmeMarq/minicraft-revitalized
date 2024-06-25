#version 450

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aUV;

out vec2 vUV;

void main() {
    gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0);
    vUV = aUV;
}
