#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;
// TODO: uniform float u_exposure

// outputs
layout (location = 0) out vec4 out_color;

vec3 RRTAndODTFit(vec3 v) {
    vec3 a = v * (v + 0.0245786) - 0.000090537;
    vec3 b = v * (0.983729 * v + 0.4329510) + 0.238081;
    return a / b;
}

void main() {
    const float gamma = 2.2;
    float u_exposure = 2.8;
    //float u_exposure = 0.5;
    vec3 hdrColor = texture(u_texture, uv).rgb;
    vec3 mapped = vec3(1.0) - exp(-hdrColor * u_exposure);
    mapped = pow(mapped, vec3(gamma));
    //mapped = pow(mapped, vec3(1.0 / gamma));
    out_color = vec4(mapped, 1.0);
}