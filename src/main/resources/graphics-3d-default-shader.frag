#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_texture_diffuse;
uniform vec4 u_color_diffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    out_color = u_color_diffuse * texture(u_texture_diffuse, uv);
}