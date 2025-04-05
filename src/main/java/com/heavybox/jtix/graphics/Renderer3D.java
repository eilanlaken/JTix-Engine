package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryPool;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

// TODO:
// draw model surface material ("regular" drawing.)
// draw model wireframe
// draw model volume (smoke, clouds, water, jelly, ...)
// render objects with opacity.

// Note: here, we can't really batch draw calls as the transform cannot be directly applied to the vertices,
// but rather sent to the GPU as a uniform u_transform.
// So the only possible optimization is sorting to minimize context switches (shader bindings).
// You must accept at least 1 draw-call per draw() operation.
// (for complex models with multiple model parts, expect more).
public class Renderer3D {

    private static final MemoryPool<RenderUnit> renderUnitPool = new MemoryPool<>(RenderUnit.class, 5);
    private static final Array<RenderUnit>      renderUnits    = new Array<>(false, 20);

    // defaults
    private static final Shader defaultShader = createDefaultShaderProgram();

    private static boolean drawing = false;

    private static Camera currentCamera = null;
    private static int    currentMode   = GL11.GL_TRIANGLES;

    // TODO: check if Renderer2D is currently rendering.
    public static void begin(Camera camera) {
        if (drawing) throw new GraphicsException("Cannot call begin() while drawing. Must call end()");
        if (camera == null) throw new GraphicsException("camera cannot be null when rendering using " + Renderer3D.class.getSimpleName() + ".begin(Camera camera).");

        GL11.glColorMask(true, true, true, true); // enable color buffer writes
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        currentCamera = camera;
        drawing = true;
    }

    public static void drawModel(Model model, Matrix4x4 transform) {

    }

    public static void drawModelWireframe(Model model, Matrix4x4 transform) {

    }

    public static void drawTexture(Texture texture, Matrix4x4 transform) {

    }

    public static void drawCubeThin(Matrix4x4 transform) {

    }

    public static void drawCubeFilled(Matrix4x4 transform) {

    }

    public static void drawText(Font font, String text, Matrix4x4 transform) {

    }

    private void flush() {

    }

    public static void end() {

    }

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources. Creating manually.");

            String vertexShader = """
                    #version 450
                        
                    // attributes
                    layout(location = 0) in vec3 a_position;
                    layout(location = 2) in vec2 a_textCoords0;
                        
                    // uniforms
                    uniform mat4 u_transform;
                    uniform mat4 u_camera_combined;
                        
                    void main() {
                        gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
                    };""";

            String fragmentShader = """
                    #version 450
                        
                    // inputs
                        
                    // uniforms
                    uniform vec4 color;
                        
                    // outputs
                    layout (location = 0) out vec4 out_color;
                        
                    void main() {
                        out_color = color;
                    }""";

            return new Shader(vertexShader, fragmentShader);
        }
    }

    private static final class RenderUnit implements MemoryPool.Reset {

        public Model.Mesh     mesh;
        public Model.Material material;
        public Matrix4x4      transform;

        @Override
        public void reset() {
            this.mesh = null;
            this.material = null;
            this.transform = null;
        }

    }

}
