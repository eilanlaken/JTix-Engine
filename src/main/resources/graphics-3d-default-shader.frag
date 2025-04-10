#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_diffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //vec2 vu = vec2(1-uv.x, 1-uv.y);
    out_color = texture(u_diffuse, uv);
}