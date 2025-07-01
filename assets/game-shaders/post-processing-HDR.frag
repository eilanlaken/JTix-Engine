#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;
// TODO: uniform float u_exposure

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    const float gamma = 2.2;
    float u_exposure = 2.3;
    vec3 hdrColor = texture(u_texture, uv).rgb;
    vec3 mapped = vec3(1.0) - exp(-hdrColor * u_exposure);
    mapped = pow(mapped, vec3(gamma));
    out_color = vec4(mapped, 1.0);
}