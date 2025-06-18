//https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.fs
#version 450

#define PI 3.1415926538
#define MAX_HEIGHT 762.318

// structs defitions
struct DirectionalLight {
    vec3 direction;
    vec3 color;
    float intensity;
};

// inputs
in vec2 uv;
in vec3 unit_vertex_to_camera;
in vec3 world_vertex_position;
in vec3 normal;

// uniforms - lights
uniform DirectionalLight directionalLights[1];

// uniforms - blend maps
uniform sampler2D u_texture_grass;
uniform sampler2D u_texture_stone;
uniform sampler2D u_texture_snow;

// outputs
layout (location = 0) out vec4 out_color;

// functions
float distribution_GGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float geometry_smith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnel_schlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

void main()
{
    float height = world_vertex_position.z;
    vec3 N = normal;

    float slope = clamp(normal.z, 0.0, 1.0); // 0 on flat ground, 1 on vertical
    slope = slope * slope * slope;
    float snowWeight = smoothstep(0, MAX_HEIGHT, height);
    vec2 scaled_uv = uv * 4.0;
    vec4 grass = texture(u_texture_grass, scaled_uv);
    vec4 rock = texture(u_texture_stone, scaled_uv);
    vec4 snow = texture(u_texture_snow, scaled_uv);
    float not_rock_factor = slope;
    float rock_factor = 1 - not_rock_factor;
    vec4 blend_map_color = (snow * snowWeight + grass * (1 - snowWeight)) * (not_rock_factor) + rock * rock_factor;
    vec3 albedo = blend_map_color.rgb;

    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, 0.0);
    vec3 Lo = vec3(0.0);

    // summation over all point light sources
    // calculate per-box2DLight radiance
    vec3 radiance = directionalLights[0].intensity * directionalLights[0].color;

    // cook-torrance brdf
    vec3 L    = normalize(-directionalLights[0].direction);
    vec3 H    = normalize(V + L);
    float NDF = distribution_GGX(N, H, 1.0);
    float G   = geometry_smith(N, V, L, 1.0);
    vec3  F   = fresnel_schlick(max(dot(H, V), 0.0), F0);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    vec3 specular     = numerator / denominator;
    //total_specular += specular;

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0;
    // add to outgoing radiance Lo
    float NdotL = max(dot(N, L), 0.0);
    Lo += (kD * albedo / PI + specular) * radiance * NdotL;

    vec3 ambient = vec3(0.4) * albedo * 1; // replace 1 with u_ao // TODO: replace 0.1 with ambient light source.
    vec3 color = ambient + Lo;
    //vec3 color = albedo;// + 0.2 * Lo;

    // HDR tonemapping
    //color = color / (color + vec3(0.05));
    // gamma correct
    //color = pow(color, vec3(1.0/2.2));
    out_color = vec4(color, 1.0);
}