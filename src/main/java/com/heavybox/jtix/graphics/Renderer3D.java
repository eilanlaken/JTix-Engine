package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

// TODO: implement.
public class Renderer3D implements MemoryResourceHolder {

    /* constants */


    /* defaults */
    private final Shader defaultShader = createDefaultShaderProgram();

    /* memory pools */
    private final MemoryPool<Vector2> vectorsPool    = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);

    /* state */
    private ComponentGraphicsCamera currentComponentGraphicsCamera = null;
    private Texture       currentTexture = null;
    private Shader currentShader  = null;
    private boolean       drawing        = false;
    private int           vertexIndex    = 0;
    private int           currentMode    = GL11.GL_TRIANGLES;
    private int           currentSFactor = GL11.GL_SRC_ALPHA;
    private int           currentDFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;
    private int           frameDrawCalls = 0;

    public Renderer3D() {

    }

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D_old.class.getClassLoader().getResourceAsStream("graphics-3d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D_old.class.getClassLoader().getResourceAsStream("graphics-3d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources. Creating manually.");

            String vertexShader = """
                    #version 450
                    
                    layout(location = 0) in vec2 a_position;
                    layout(location = 1) in vec4 a_color;
                    layout(location = 2) in vec2 a_texCoord0;

                    uniform mat4 u_camera_combined;

                    out vec4 color;
                    out vec2 uv;

                    void main() {
                        color = a_color;
                        uv = a_texCoord0;
                        gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);
                    };""";

            String fragmentShader = """
                    #version 450

                    in vec4 color;
                    in vec2 uv;
                   
                    uniform sampler2D u_texture;
                    
                    layout (location = 0) out vec4 out_color;
                    
                    void main() {
                        out_color = texture(u_texture, uv);
                    }""";

            return new Shader(vertexShader, fragmentShader);
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
