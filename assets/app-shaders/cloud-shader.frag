#version 450
#define FRAME 1
// inputs
in vec2 uv;
in float distance_camera_to_vertex;

// uniforms - PBR material
uniform sampler2D u_texture_opacity;
uniform sampler2D u_texture_atlas;
uniform float u_time;
uniform int u_frame;

// outputs
layout (location = 0) out vec4 out_color;

vec2 remap_uv(vec2 uv, int frame, int gridSize)
{
    float frameX = float(frame % gridSize);
    float frameY = float(frame / gridSize);
    vec2 cellSize = vec2(1.0 / float(gridSize));
    return uv * cellSize + vec2(frameX, frameY) * cellSize;
}

void main()
{
    float fadeStart = 4.0; // distance where fading begins
    float fadeEnd = 2.5; // TODO
    float fade = clamp((distance_camera_to_vertex - fadeEnd) / (fadeStart - fadeEnd), 0.0, 1.0);
    vec2 newUV = remap_uv(uv, u_frame, 8); // for 8x8 atlas
    vec4 color = texture(u_texture_atlas, newUV);
    color.a *= fade;
    if (color.a < 0.001) discard;
    out_color = color;
}