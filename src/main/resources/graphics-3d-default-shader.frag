#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_texture_diffuse;
uniform vec4 u_color_diffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //vec2 vu = vec2(1-uv.x, 1-uv.y);
    out_color = u_color_diffuse * texture(u_texture_diffuse, uv);
}