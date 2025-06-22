// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define MAX_HEIGHT 55
#define TILE_SIZE 256.0

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;
uniform sampler2D u_texture_height_map;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out vec3 normal;

float getHeight(vec2 uv)
{
    return (2.0 * texture(u_texture_height_map, uv).r - 1.0) * MAX_HEIGHT;
}

void main()
{
    //float height = (2 * texture(u_texture_height_map, a_textCoords0).r - 1) * MAX_HEIGHT;
    float height = getHeight(a_textCoords0);
    vec4 vertex_position =  u_transform * vec4(a_position.x, a_position.y, height, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    vec3 offset = vec3(1.0 / TILE_SIZE, 1.0 / TILE_SIZE, 0);
    float hL = getHeight(a_textCoords0 - offset.xz);
    float hR = getHeight(a_textCoords0 + offset.xz);
    float hD = getHeight(a_textCoords0 - offset.zy);
    float hU = getHeight(a_textCoords0 + offset.zy);
    // deduce terrain normal
    vec3 N;
    N.x = hL - hR;
    N.y = hD - hU;
    N.z = 2.0;
    N = normalize(N);
    mat3 normal_matrix = mat3(transpose(inverse(u_transform)));
    normal = vec3(normal_matrix * N);

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    world_vertex_position = vertex_position.xyz;
    uv = a_textCoords0;
}
