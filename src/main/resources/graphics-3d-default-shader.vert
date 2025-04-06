#version 450

// attributes
layout(location = 0) in vec3 a_position;

// uniforms
//uniform mat4 u_transform;
//uniform mat4 u_camera_combined;

void main() {
    //gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
    gl_Position = vec4(a_position, 1.0);
}