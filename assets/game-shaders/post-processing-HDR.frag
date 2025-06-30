#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    const float gamma = 2.2;
    vec3 hdrColor = texture(u_texture, uv).rgb;
    vec3 mapped = vec3(1.0) - exp(-hdrColor * 5.0f);
    out_color = vec4(mapped, 1.0);

//    const float gamma = 2.2;
//    vec3 hdrColor = texture(u_texture, uv).rgb;
//    vec3 mapped = hdrColor / (hdrColor + vec3(1.0));
//    mapped = pow(mapped, vec3(1.0 / gamma));
//    out_color = vec4(mapped, 1.0);
}