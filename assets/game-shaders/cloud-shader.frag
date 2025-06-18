#version 450

// inputs
in vec2 uv;
in float distance_camera_to_vertex;

// uniforms - PBR material
uniform sampler2D u_texture_atlas;

// outputs
layout (location = 0) out vec4 out_color;

void main()
{
    float fadeStart = 100.0; // distance where fading begins
    float fadeEnd = 20.0; // TODO
    float fade = clamp((distance_camera_to_vertex - fadeEnd) / (fadeStart - fadeEnd), 0.0, 1.0);
    vec4 color = texture(u_texture_atlas, uv);
    color.a *= fade * 0.8;
    out_color = color;
}