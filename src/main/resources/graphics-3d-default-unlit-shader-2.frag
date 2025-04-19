#version 450

// inputs
in vec2 uv;

// uniforms - PBR material
uniform sampler2D u_texture_diffuse;
uniform vec4 u_color_diffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main()
{
    vec3 albedo = (u_color_diffuse * texture(u_texture_diffuse, uv)).rgb;
    out_color = vec4(albedo, 1.0);
}