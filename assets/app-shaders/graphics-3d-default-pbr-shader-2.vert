// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;
layout(location = 4) in vec3 a_normal;
layout(location = 5) in vec3 a_tangent;;
layout(location = 6) in vec3 a_biTangent;;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out mat3 invTBN; // invTBN is a matrix that transforms vectors from xyz space to tbn space

void main()
{
    vec4 vertex_position = u_transform * vec4(a_position, 1.0);
    gl_Position = u_camera_combined * vertex_position;
    mat3 normal_matrix = mat3(transpose(inverse(u_transform)));
    vec3 T = normalize(vec3(normal_matrix * a_tangent));
    vec3 B = normalize(vec3(normal_matrix * a_biTangent));
    vec3 N = normalize(vec3(normal_matrix * a_normal));
    mat3 TBN = mat3(T, B, N);
    invTBN = transpose(TBN); // TBN is orthogonal therefore inverse(TBN) = transpose(TBN)

    unit_vertex_to_camera = normalize(invTBN * (u_camera_position - vertex_position.xyz));
    world_vertex_position = vertex_position.xyz;
    uv = a_textCoords0;

//    uv = a_textCoords0;
//    world_vertex_position = vec3(u_transform * vec4(a_position, 1.0));
//    world_vertex_normal = mat3(transpose(inverse(u_transform))) * a_normal; // handle non-uniform scalings.
//    gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
}
