#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_diffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    out_color = texture(u_diffuse, uv);
}