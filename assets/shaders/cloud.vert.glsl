#version 450

layout(location = 0) in vec3 a_position;

uniform mat4 u_transform;
uniform mat4 u_camera_combined;

out vec3 worldPos;

void main() {
    vec4 world = u_transform * vec4(a_position, 1.0);
    worldPos = world.xyz;
    gl_Position = u_camera_combined * world;
}
