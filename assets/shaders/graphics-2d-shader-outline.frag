#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;
//uniform int n;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    vec4 outlineColor = vec4(0.204, 0.184, 0.114, 1.0);
    vec4 texColor = texture(u_texture, uv);

    // If the current pixel is not transparent, render normally
    if (texColor.a > 0) {
        out_color = color * texColor;
        return;
    }

    vec2 size = textureSize(u_texture, 0);
    float texelWidth = 1.0 / size.x;
    float texelHeight = 1.0 / size.y;

//    // Sample neighboring pixels
//    float left   = texture(u_texture, uv + vec2(texelWidth, 0)).a;
//    float right  = texture(u_texture, uv + vec2(-texelWidth, 0)).a;
//    float top    = texture(u_texture, uv + vec2(0, texelHeight)).a;
//    float bottom = texture(u_texture, uv + vec2(0, -texelHeight)).a;
//
//    // If any neighbor is opaque, draw outline
//    if (left > 0.1 || right > 0.1 || top > 0.1 || bottom > 0.1) {
//        out_color = outlineColor;
//    } else {
//        out_color = texColor;  // Keep it transparent
//    }





    // Loop over the n x n neighborhood
    int n = 8;
    int halfN = n / 2;
    vec2 texelSize = vec2(texelWidth, texelHeight);

    // Check the surrounding pixels in the n x n region
    int count = 0;
    for (int i = -halfN; i <= halfN; ++i) {
        for (int j = -halfN; j <= halfN; ++j) {
            vec4 neighborColor = texture(u_texture, uv + vec2(i, j) * texelSize);
            if (neighborColor.a > 0.1) {
                count++;
            }
        }
    }

    // If any neighbor is opaque, draw the outline
    if (count > 0) {
        float alpha = float(count) / float(n);  // Normalize to the size of the n x n region
        out_color = vec4(outlineColor.rgb, outlineColor.a * alpha);
        //out_color.a = alpha;
    } else {
        out_color = texColor;  // Keep the original transparent color
    }

}