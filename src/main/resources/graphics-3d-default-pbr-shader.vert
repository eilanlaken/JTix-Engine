// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;
layout(location = 4) in vec3 a_normal;;

// uniforms
uniform mat4 u_transform;
//uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;

// outputs
out vec3 world_vertex_position;
out vec3 world_vertex_normal;
out vec2 uv;

void main()
{
    uv = a_textCoords0;
    world_vertex_position = vec3(u_transform * vec4(a_position, 1.0));
    world_vertex_normal = mat3(transpose(inverse(u_transform))) * a_normal; // handle non-uniform scalings.
    gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
}
