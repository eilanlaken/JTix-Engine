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
    world_vertex_normal = vec3(u_transform * vec4(a_normal, 1.0)); //
    gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
}

// TODO: NOTE: THIS IS INCORRECT. In case the transform contains
// TODO: NOTE: non-uniform scaling or shearing, this will not transform
// TODO: NOTE: the normals properly. below is the correct transform:
// world_vertex_normal = normal_matrix * a_normal; // where normal_matrix is: transpose(inverse(mat3(u_transform))); bound in the CPU.