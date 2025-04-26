#version 450
#define FRAME 1
// inputs
in vec2 uv;

// uniforms - PBR material
uniform sampler2D u_texture_opacity;
uniform sampler2D u_texture_atlas;
uniform float u_time;
uniform int u_frame;

// outputs
layout (location = 0) out vec4 out_color;

// TODO: set the opacity based on the distance from camera.

vec2 remap_uv(vec2 uv, int frame, int gridSize) {
    float frameX = float(frame % gridSize);
    float frameY = float(frame / gridSize);
    vec2 cellSize = vec2(1.0 / float(gridSize));
    return uv * cellSize + vec2(frameX, frameY) * cellSize;
}

int get_current_frame(float fps) {
    return int(floor(u_time * fps)) % 64;
}

void main()
{
//    float alpha = texture(u_texture_opacity, uv).a;
//    if (alpha< 0.001) discard;
//    out_color = vec4(0,0,0,alpha);


    int frame = u_frame;
    vec2 newUV = remap_uv(uv, frame, 8); // for 8x8 atlas
    vec4 color = texture(u_texture_atlas, newUV);
    if (color.a < 0.001) discard;
    out_color = color;

//    float alpha_circle = texture(u_texture_opacity, uv).a;
//    vec2 newUV = remap_uv(uv, u_frame, 8); // for 8x8 atlas
//    float alpha_cloud = texture(u_texture_atlas, newUV).a;
//    float opacity = min(alpha_circle, alpha_cloud);
//    if (opacity < 0.001) discard;
//    out_color = vec4(1,1,1,opacity);
}