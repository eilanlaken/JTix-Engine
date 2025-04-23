#version 330 core

in vec3 worldPos;

// uniforms
uniform vec3 u_camera_position;
uniform float time;

out vec4 FragColor;

// Simple noise function (replace with 3D noise or texture for better results)
float noise(vec3 p) {
    return fract(sin(dot(p ,vec3(12.9898,78.233,37.719))) * 43758.5453);
}

void main() {
    float density = 0.8;
    vec3 cloudColor = vec3(1.0, 1.0, 1.0);
    float thickness = 0.1 * length(u_camera_position - worldPos);

    float transmittance = exp(-density * thickness);
    vec3 color = cloudColor * (1.0 - transmittance);
    FragColor = vec4(color, 1.0 - transmittance); // alpha = absorption
}
