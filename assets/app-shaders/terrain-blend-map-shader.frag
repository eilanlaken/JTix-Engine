#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_texture_background;
uniform sampler2D u_texture_red;
uniform sampler2D u_texture_green;
uniform sampler2D u_texture_blue;
uniform sampler2D u_texture_blend_map;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    vec4 blend_map_color = texture(u_texture_blend_map, uv);

    float background_weight = 1 - (blend_map_color.r + blend_map_color.g + blend_map_color.b);
    vec2 scaled_uv = uv * 2.0;
    vec4 background_color = texture(u_texture_background, scaled_uv) * background_weight;
    vec4 r_color = texture(u_texture_red, scaled_uv) * blend_map_color.r;
    vec4 g_color = texture(u_texture_green, scaled_uv) * blend_map_color.g;
    vec4 b_color = texture(u_texture_blue, scaled_uv) * blend_map_color.b;

    vec4 total_color = background_color + r_color + g_color + b_color;

    out_color = total_color;
}