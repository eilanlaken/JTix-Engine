#version 450

#define PI 3.1415926538
#define NUM_POINT_LIGHTS 2

// structs defitions
struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;
    float intensity;
};

// inputs
in vec2 uv;
in mat3 invTBN;
in vec3 unit_vertex_to_camera;
in vec3 world_vertex_position;

// uniforms - lights
uniform PointLight pointLights[NUM_POINT_LIGHTS];
uniform DirectionalLight directionalLight;

// uniforms - PBR material
uniform sampler2D u_texture_diffuse;
uniform sampler2D u_texture_normalMap;
uniform vec4 u_color_diffuse;
uniform float u_prop_metallic; // TODO: add texture
uniform float u_prop_roughness; // TODO: add texture

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
    vec3 albedo = (u_color_diffuse * texture(u_texture_diffuse, uv)).rgb;
    vec3 N = normalize(texture(u_texture_normalMap, uv).rgb * 2.0 - 1.0);
    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, u_prop_metallic);

    vec3 Lo = vec3(0.0);

    // summation over all point light sources
    // calculate per-box2DLight radiance
    for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
        vec3 vertex_to_light = invTBN * (pointLights[i].position - world_vertex_position);
        float distance_to_light = length(vertex_to_light);
        float attenuation = 1.0 / (1.0 + 0.1 * distance_to_light + 0.01 * distance_to_light * distance_to_light);
        vec3 radiance = pointLights[i].intensity * pointLights[i].color * attenuation;

        // cook-torrance brdf
        vec3 L = normalize(vertex_to_light);
        vec3 H = normalize(V + L);
        float NDF = distribution_GGX(N, H, u_prop_roughness);
        float G = geometry_smith(N, V, L, u_prop_roughness);
        vec3 F = fresnel_schlick(max(dot(H, V), 0.0), F0);

        vec3 numerator = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular = numerator / denominator;
        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - u_prop_metallic;
        // add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // add ambient light
    vec3 ambient = vec3(0.03) * albedo * 1; // replace 1 with u_ao // TODO: replace 0.1 with ambient light source.
    vec3 color = ambient + Lo;

    // HDR tonemapping
    color = color / (color + vec3(0.05));
    // gamma correct
    //color = pow(color, vec3(1.0/2.2));
    out_color = vec4(color, 1.0);
}