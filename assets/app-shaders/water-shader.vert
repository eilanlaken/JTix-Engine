// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define PI 3.1415926538
#define MAX_HEIGHT 25
#define TILE_SIZE 256.0

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;
uniform float time;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out vec3 normal;

float getHeight(float x, float y) {
    return MAX_HEIGHT * sin(PI * x / (TILE_SIZE * 2)) * sin(PI * y / (TILE_SIZE * 2)) * sin(time);
}

void main()
{
    //float height = (2 * texture(u_texture_height_map, a_textCoords0).r - 1) * MAX_HEIGHT;
    float height = getHeight(a_position.x, a_position.y);
    vec4 vertex_position =  u_transform * vec4(a_position.x, a_position.y, height, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    float delta = 4.0315; // the dx,dy between the x and y coordinates on the mesh.
    float hL = getHeight(a_position.x - delta, a_position.y);
    float hR = getHeight(a_position.x + delta, a_position.y);
    float hD = getHeight(a_position.x, a_position.y - delta);
    float hU = getHeight(a_position.x, a_position.y + delta);
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
