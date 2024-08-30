package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResourceHolder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Renderer3D implements MemoryResourceHolder {

    private static ShaderProgram createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new ShaderProgram(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources. Creating manually.");

            String vertexShader = """
                    #version 330
                    
                    layout(location = 0) in vec3 a_position;
                    layout(location = 1) in vec2 a_texCoord0;
                    layout(location = 2) in vec2 a_texCoord1;
                    layout(location = 3) in vec3 a_normal;
                    
                    uniform mat4 u_body_transform;
                    uniform vec3 u_camera_position;
                    uniform mat4 u_camera_combined; // proj * view
                  
                    out vec3 unit_vertex_to_camera;
                    out vec3 unit_world_normal;
                    out vec3 world_vertex_position;
                    out vec2 uv;
                    
                    void main() {
                    
                        vec4 vertex_position = u_body_transform * vec4(a_position, 1.0);
                        gl_Position = u_camera_combined * vertex_position;
                    
                    
                        unit_vertex_to_camera = normalize(u_camera_position - vertex_position.xyz);
                        unit_world_normal = normalize((u_body_transform * vec4(a_normal, 1.0)).xyz);
                        world_vertex_position = vertex_position.xyz;
                        uv = a_texCoord0;
                    }""";

            String fragmentShader = """
                    #version 450

                    // inputs
                    in vec4 color;
                    in vec2 uv;

                    // uniforms
                    uniform sampler2D u_texture;

                    // outputs
                    layout (location = 0) out vec4 out_color;

                    void main() {
                        out_color = color * texture(u_texture, uv);
                    }""";

            return new ShaderProgram(vertexShader, fragmentShader);
        }
    }

    @Override
    public void deleteAll() {
//        defaultShader.delete();
//        GL30.glDeleteVertexArrays(vao);
//        GL30.glDeleteBuffers(vbo);
//        GL30.glDeleteBuffers(ebo);
//        whitePixel.delete();
    }

}
