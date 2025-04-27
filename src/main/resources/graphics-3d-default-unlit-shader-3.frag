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
    vec4 albedo = u_color_diffuse * texture(u_texture_diffuse, uv);
    //if (albedo.a < 0.001) discard;
    //out_color = vec4(albedo.rgb, 1.0);
    out_color = albedo;
}