#version 450

// inputs

// uniforms
uniform vec4 color;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    out_color = color;
}