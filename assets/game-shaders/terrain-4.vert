// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define MAX_HEIGHT 400
#define MIN_HEIGHT -20
#define MAP_SIZE 4096.0
#define TILE_SIZE 256.0

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
out vec2 uv_geometry;
out vec2 uv_colors;
out vec3 normal;
out float height;

float getHeight(vec2 uv)
{
    return mix(MIN_HEIGHT, MAX_HEIGHT, texture(u_texture_height_map, uv).r);
}

void main()
{
    // TODO
    const int tiles_per_row = 8;
    const float tile_size = 1.0 / float(tiles_per_row);
    vec2 offset_uv = vec2(float(u_tile_index_col), float(u_tile_index_row)) * tile_size;
    uv_geometry = a_textCoords0 * tile_size + offset_uv;


    height = getHeight(uv_geometry);
    vec4 vertex_position =  u_transform * vec4(a_position.x, a_position.y, height, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    vec3 offset = vec3(1.0 / TILE_SIZE, 1.0 / TILE_SIZE, 0);
    float hL = getHeight(uv_geometry - offset.xz);
    float hR = getHeight(uv_geometry + offset.xz);
    float hD = getHeight(uv_geometry - offset.zy);
    float hU = getHeight(uv_geometry + offset.zy);

//    vec3 offset = vec3(1.0 / MAP_SIZE, 1.0 / MAP_SIZE, 0);
//    float hL = getHeight(a_textCoords0 - offset.xz);
//    float hR = getHeight(a_textCoords0 + offset.xz);
//    float hD = getHeight(a_textCoords0 - offset.zy);
//    float hU = getHeight(a_textCoords0 + offset.zy);

    // deduce terrain normal
    vec3 N;
    N.x = hL - hR;
    N.y = hD - hU;
    N.z = 2.0;
    N = normalize(N);
    mat3 normal_matrix = mat3(transpose(inverse(u_transform))); // TODO: use transform on the normal. No need to invert.
    normal = vec3(normal_matrix * N);

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    world_vertex_position = vertex_position.xyz;
    uv_colors = a_textCoords0;
}
