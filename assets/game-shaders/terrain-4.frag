//https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.fs
#version 450

#define PI 3.1415926538
#define MAX_HEIGHT 200
#define MIN_HEIGHT -20

// structs defitions
struct DirectionalLight {
    vec3 direction;
    vec3 color;
    float intensity;
};

// inputs
in vec2 uv;
in vec2 uv_geometry;
in vec2 uv_colors;
in vec3 unit_vertex_to_camera;
in vec3 world_vertex_position;
in vec3 normal;
in float height;

// uniforms - lights
uniform DirectionalLight directionalLights[1];

// uniforms - blend maps
uniform sampler2D u_texture_steep;
uniform sampler2D u_texture_background;
uniform sampler2D u_texture_red;
uniform sampler2D u_texture_green;
uniform sampler2D u_texture_blue;
uniform sampler2D u_texture_blend_map;
uniform sampler2D u_texture_height_map;


// outputs
layout (location = 0) out vec4 out_color;

// TODO: maybe blend to water color, not alpha.
float getAlpha()
{
    //float alpha = smoothstep(MIN_HEIGHT, -5.0, height);
    float alpha = smoothstep(MIN_HEIGHT, -6.0f, height);
    return alpha;
}

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
    /* total color calculation */
    vec4 blend_map_color = texture(u_texture_blend_map, uv_geometry);
    float background_weight = 1 - (blend_map_color.r + blend_map_color.g + blend_map_color.b);
    vec2 scaled_uv = uv_colors * 4.0;

    // select one.

//    vec3 background_color = pow(texture(u_texture_background, scaled_uv).rgb, vec3(2.2)) * background_weight;
//    vec3 r_color = pow(texture(u_texture_red, scaled_uv).rgb, vec3(2.2)) * blend_map_color.r;
//    vec3 g_color = pow(texture(u_texture_green, scaled_uv).rgb, vec3(2.2)) * blend_map_color.g;
//    vec3 b_color = pow(texture(u_texture_blue, scaled_uv).rgb, vec3(2.2)) * blend_map_color.b;

    vec3 background_color = texture(u_texture_background, scaled_uv).rgb * background_weight;
    vec3 r_color = texture(u_texture_red, scaled_uv).rgb * blend_map_color.r;
    vec3 g_color = texture(u_texture_green, scaled_uv).rgb * blend_map_color.g;
    vec3 b_color = texture(u_texture_blue, scaled_uv).rgb * blend_map_color.b;

    vec3 total_color = background_color + r_color + g_color + b_color;
    float t = clamp(normal.z, 0.0, 1.0); // 0 on flat ground, 1 on vertical
    float smoothT = t * t * (3.0 - 2.0 * t);
    //vec3 albedo = total_color.rgb * (t) + (1- t) * texture(u_texture_steep, uv).rgb;
    vec3 albedo = mix(total_color.rgb, texture(u_texture_steep, scaled_uv).rgb, 1 - smoothT);

    /* Directional light calculation */
    vec3 N = normal;
    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, 0.0);
    vec3 Lo = vec3(0.0);
    vec3 radiance = directionalLights[0].intensity * directionalLights[0].color;
    vec3 L    = normalize(-directionalLights[0].direction);
    vec3 H    = normalize(V + L);
    float NDF = distribution_GGX(N, H, 1.0);
    float G   = geometry_smith(N, V, L, 1.0);
    vec3  F   = fresnel_schlick(max(dot(H, V), 0.0), F0);
    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    vec3 specular     = numerator / denominator;
    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0;
    float NdotL = max(dot(N, L), 0.0);
    Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    vec3 ambient = vec3(0.4) * albedo;
    vec3 color = ambient + Lo;

    float alpha = getAlpha();
    out_color = vec4(color, 1.0);
}