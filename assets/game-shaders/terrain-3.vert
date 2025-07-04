// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define MAX_HEIGHT 80
#define MIN_HEIGHT -12
#define TILE_SIZE 512.0
#define MAP_SIZE 4096.0

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;
uniform sampler2D u_texture_height_map;
uniform int u_tile_index_row;
uniform int u_tile_index_col;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out vec3 normal;
out float height;

float getHeight(vec2 uv)
{
    //return (2.0 * texture(u_texture_height_map, uv).r - 1.0) * MAX_HEIGHT;
    return mix(MIN_HEIGHT, MAX_HEIGHT, texture(u_texture_height_map, uv).r);
}

void main()
{
    const int tiles_per_row = 8; // TODO: #define
    const float tile_size = 1.0 / float(tiles_per_row);
    vec2 offset_uv = vec2(float(u_tile_index_col), float(u_tile_index_row)) * tile_size;
    uv = a_textCoords0 * tile_size + offset_uv;

    height = getHeight(uv);
    vec4 vertex_position =  u_transform * vec4(a_position.x, a_position.y, height, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    vec3 offset = vec3(1.0 / TILE_SIZE, 1.0 / TILE_SIZE, 0);
    float hL = getHeight(uv - offset.xz);
    float hR = getHeight(uv + offset.xz);
    float hD = getHeight(uv - offset.zy);
    float hU = getHeight(uv + offset.zy);
    // deduce terrain normal
    vec3 N;
    N.x = hL - hR;
    N.y = hD - hU;
    N.z = 2.0;
    N = normalize(N);
    mat3 normal_matrix = mat3(transpose(inverse(u_transform))); // TODO: remove
    normal = vec3(normal_matrix * N); // normalize(vec3(u_transform * vec4(N, 0.0f))); // TODO: replace

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    world_vertex_position = vertex_position.xyz;
}
