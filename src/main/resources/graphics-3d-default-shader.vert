#version 330

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in vec2 a_texCoord1;
layout(location = 3) in vec3 a_normal;

uniform mat4 u_body_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined; // proj * view

out vec3 unit_vertex_to_camera;
out vec3 unit_world_normal;
out vec3 world_vertex_position;
out vec2 uv;

void main() {
    vec4 vertex_position = u_body_transform * vec4(a_position, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    unit_world_normal = normalize((u_body_transform * vec4(a_normal, 1.0)).xyz);
    world_vertex_position = vertex_position.xyz;
    uv = a_texCoord0;
}