// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450

#define PI 3.1415926538

// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;

uniform float time;
uniform float uWavesAmplitude;
uniform float uWavesSpeed;
uniform float uWavesFrequency;
uniform float uWavesPersistence;
uniform float uWavesLacunarity;
uniform int uWavesIterations;

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out vec3 normal;
out float vElavation;

vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float snoise(vec2 v)
{
    const vec4 C = vec4(0.211324865405187, 0.366025403784439,
    -0.577350269189626, 0.024390243902439);
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);
    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod(i, 289.0);
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
                      + i.x + vec3(0.0, i1.x, 1.0 ));
    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
    dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float getElevation(vec3 p)
{
    float a = 1.0f;
    float f = uWavesFrequency;
    float total = 0.0f;

    float dist = length(u_camera_position - p);
    float maxDist = 4200.0;
    float attenuation = smoothstep(maxDist, 0.0, dist);
    attenuation = mix(0.1, 1.0, attenuation); // remap to [0.5,1.0]
    float amp = uWavesAmplitude * attenuation;

    for (int i = 0; i < uWavesIterations; i++)
    {
        total += amp * snoise(f * p.xy + uWavesSpeed * time);
        a *= uWavesPersistence;
        f *= uWavesLacunarity;
    }

    return uWavesAmplitude * total;
}

void main()
{
    vec4 vertex_position =  u_transform * vec4(a_position, 1.0);
    float elavation = getElevation(vertex_position.xyz);
    vertex_position.z += elavation;
    gl_Position = u_camera_combined * vertex_position;

//    float eps = 1.0f / 2000.0f;
//    vec3 p = vertex_position.xyz;
//    vec3 px = vec3(p.x + eps, p.y, getElevation(vec3(p.x + eps,p.y, 0)));
//    vec3 py = vec3(p.x, p.y + eps, getElevation(vec3(p.x,p.y + eps, 0)));
//    vec3 tangent = normalize(px - p);
//    vec3 bitangent = normalize(py - p);
//    normal = cross(tangent, bitangent);

    float eps = 1.0f / 2000.0f;
    vec3 p = vertex_position.xyz;
    vec3 offset = vec3(eps, eps, 0);
    float hL = getElevation(p - offset.xzz);
    float hR = getElevation(p + offset.xzz);
    float hD = getElevation(p - offset.zyz);
    float hU = getElevation(p + offset.zyz);

    // deduce terrain normal
    vec3 N;
    N.x = hL - hR;
    N.y = hD - hU;
    N.z = 2.0;
    normal = normalize(N);

    unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
    world_vertex_position = vertex_position.xyz;
    uv = a_textCoords0;
    vElavation = elavation;
}
