//https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.fs
#version 450

const float PI = 3.14159265359;

// structs defitions
struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
};

// inputs
in vec2 uv;
in vec3 world_vertex_position;
in vec3 world_vertex_normal;

// uniforms - camera
uniform vec3 u_camera_position;

// uniforms - lights
uniform PointLight pointLight;

// uniforms - PBR material
uniform sampler2D u_texture_diffuse;
uniform vec4 u_color_diffuse;
uniform float u_prop_metallic; // TODO: add texture
uniform float u_prop_roughness; // TODO: add texture

// outputs
layout (location = 0) out vec4 out_color;

// functions
float DistributionGGX(vec3 N, vec3 H, float roughness)
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
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

void main()
{
    vec3 N = normalize(world_vertex_normal);
    vec3 V = normalize(u_camera_position - world_vertex_position);

    // calculate reflectance at normal incidence; if dia-electric (like plastic) use F0
    // of 0.04 and if it's a metal, use the albedo color as F0 (metallic workflow)
    vec3 F0 = vec3(0.04);
    vec3 albedo = (u_color_diffuse * texture(u_texture_diffuse, uv)).rgb;
    F0 = mix(F0, albedo, u_prop_metallic);

    vec3 Lo = vec3(0.0);
    // for: i = 0...NUM_LIGHTS
    // per light radiance
    vec3 L = normalize(pointLight.position - world_vertex_position);
    vec3 H = normalize(V + L);
    float distance = length(pointLight.position - world_vertex_position);
    //float attenuation = pointLight.intensity / (distance * distance);
    float attenuation = pointLight.intensity / (1.0 + 0.1 * distance + 0.01 * distance * distance);
    vec3 radiance = pointLight.color * attenuation;

    // cook torrance BRDF
    float NDF = DistributionGGX(N, H, u_prop_roughness);
    float G   = GeometrySmith(N, V, L, u_prop_roughness);
    vec3 F    = fresnelSchlick(clamp(dot(H, V), 0.0, 1.0), F0);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular     = numerator / denominator;

    // kS is equal to Fresnel
    vec3 kS = F;
    // for energy conservation, the diffuse and specular light can't
    // be above 1.0 (unless the surface emits light); to preserve this
    // relationship the diffuse component (kD) should equal 1.0 - kS.
    vec3 kD = vec3(1.0) - kS;
    // multiply kD by the inverse metalness such that only non-metals
    // have diffuse lighting, or a linear blend if partly metal (pure metals
    // have no diffuse light).
    kD *= 1.0 - u_prop_metallic;

    // scale light by NdotL
    float NdotL = max(dot(N, L), 0.0);

    // add to outgoing radiance Lo
    Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    // endfor

    // ambient lighting (note that the next IBL tutorial will replace
    // this ambient lighting with environment lighting).
    vec3 ambient = vec3(0.1) * albedo * 1; // replace 1 with u_ao // TODO: replace 0.1 with ambient light source.

    vec3 color = ambient + Lo;

    // HDR tonemapping
    //color = color / (color + vec3(1.0));
    // gamma correct
    //color = pow(color, vec3(1.0/2.2));
    out_color = vec4(color, 1.0);
}