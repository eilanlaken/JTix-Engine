#version 450

#define MAX_HEIGHT 25

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_transform;
uniform mat4 u_camera_combined;
uniform sampler2D u_texture_height_map;

out vec2 uv;

void main() {
    uv = a_textCoords0;
    float height = (2 * texture(u_texture_height_map, a_textCoords0).r - 1) * MAX_HEIGHT;
    gl_Position = u_camera_combined * u_transform * vec4(a_position.x, a_position.y, height, 1.0);

    // TODO: calculate the normal to the surface.
}