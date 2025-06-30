//https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.fs
#version 450

#define PI 3.1415926538

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
in float vElavation;


// uniforms - lights
uniform DirectionalLight directionalLights[1];

// uniforms - blend maps
uniform vec4 uTroughColor;
uniform vec4 uSurfaceColor;
uniform vec4 uPeakColor;

uniform float uPeakThreshold;
uniform float uPeakTransition;
uniform float uTroughThreshold;
uniform float uTroughTransition;

uniform float time;

// outputs
layout (location = 0) out vec4 out_color;

// TODO: maybe blend to water color, not alpha.
float getAlpha()
{
    //float alpha = smoothstep(MIN_HEIGHT, -5.0, height);
    float alpha = smoothstep(vElavation, -6.0f, vElavation);
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
    float trough2surface = smoothstep(uTroughThreshold - uTroughTransition, uTroughThreshold + uTroughTransition, vElavation);
    float surface2peak = smoothstep(uPeakThreshold - uPeakTransition, uPeakThreshold + uPeakTransition, vElavation);
    vec3 mixedColor1 = mix(uTroughColor, uSurfaceColor, trough2surface).rgb;
    vec3 mixedColor2 = mix(mixedColor1, uPeakColor.rgb, surface2peak).rgb;
    vec3 albedo = mixedColor2;

    //vec3 N = normalize(texture(u_texture_normalMap, uv).rgb * 2.0 - 1.0);
    vec3 N = normal;//normalize(vec3(0,0,1)); // always up for now, will be calculated from the vertex shader and passed as in variable
    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, 0.0);

    vec3 Lo = vec3(0.0);

    // summation over all point light sources
    // calculate per-box2DLight radiance
    float light_intensity_adjacment = 0.2f;
    vec3 radiance = light_intensity_adjacment * directionalLights[0].intensity * directionalLights[0].color;

    // cook-torrance brdf
    vec3 L    = normalize(-directionalLights[0].direction);
    vec3 H    = normalize(V + L);
    float NDF = distribution_GGX(N, H, 0.0);
    float G   = geometry_smith(N, V, L, 0.0);
    vec3  F   = fresnel_schlick(max(dot(H, V), 0.0), F0);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
    vec3 specular     = numerator / denominator;

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0;
    // add to outgoing radiance Lo
    float NdotL = max(dot(N, L), 0.0);
    Lo += (kD * albedo / PI + specular) * radiance * NdotL;

    vec3 ambient = vec3(0.4) * albedo * 1; // replace 1 with u_ao // TODO: replace 0.1 with ambient light source.
    vec3 color = ambient + Lo;

    //out_color = vec4(color, 0.8f);
    out_color = vec4(color, 1);
}