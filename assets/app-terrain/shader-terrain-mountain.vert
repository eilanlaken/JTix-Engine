// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define MAX_HEIGHT 25
#define TILE_SIZE 256.0

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;
layout(location = 4) in vec3 a_normal;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out vec3 normal;

void main()
{
    //float height = (2 * texture(u_texture_height_map, a_textCoords0).r - 1) * MAX_HEIGHT;
    vec4 vertex_position =  u_transform * vec4(a_position, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    mat3 normal_matrix = mat3(transpose(inverse(u_transform)));
    normal = vec3(normal_matrix * a_normal);

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    world_vertex_position = vertex_position.xyz;
    uv = a_textCoords0;
}
