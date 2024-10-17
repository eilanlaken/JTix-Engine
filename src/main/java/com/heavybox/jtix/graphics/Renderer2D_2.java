package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Renderer2D_2 implements MemoryResourceHolder {

    /* constants */
    private static final int   VERTEX_SIZE       = 5;    // A vertex is composed of 5 floats: x,y: position, t: color (as float bits) and u,v: texture coordinates.
    private static final int   VERTICES_CAPACITY = 20000; // The batch can render VERTICES_CAPACITY vertices (so wee need a float buffer of size: VERTICES_CAPACITY * VERTEX_SIZE)
    private static final int   INDICES_CAPACITY  = 20000;
    private static final float WHITE_TINT        = Color.WHITE.toFloatBits();

    /* buffers */
    private final int         vao;
    private final int         vbo;
    private final int         ebo;
    private final IntBuffer   indicesBuffer  = BufferUtils.createIntBuffer(INDICES_CAPACITY * 3);
    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * VERTEX_SIZE);

    /* defaults */
    private final Shader    defaultShader = createDefaultShaderProgram();
    private final Texture   whitePixel    = createWhiteSinglePixelTexture();
    private final Matrix4x4 defaultMatrix = createDefaultMatrix();

    /* memory pools */
    private final MemoryPool<Vector2>    vectors2Pool   = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);

    /* state */
    private Matrix4x4 currentMatrix     = null;
    private Texture   currentTexture    = null;
    private Shader    currentShader     = null;
    private float     currentTint       = WHITE_TINT;
    private boolean   drawing           = false;
    private int       vertexIndex       = 0;
    private int       currentMode       = GL11.GL_TRIANGLES;
    private int       currentSFactor    = GL11.GL_SRC_ALPHA;
    private int       currentDFactor    = GL11.GL_ONE_MINUS_SRC_ALPHA;
    private int       perFrameDrawCalls = 0;

    /* Vertex Buffers */

    private VertexBuffer defaultVertexBuffer = new VertexBuffer(10000);

    public Renderer2D_2() {
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        {
            this.vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
            int vertexSizeBytes = VERTEX_SIZE * Float.BYTES;
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSizeBytes, 0); // positions
            GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSizeBytes, Float.BYTES * 2L); // colors
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSizeBytes, Float.BYTES * 3L); // uvs
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            this.ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
        }
        GL30.glBindVertexArray(0);
    }

    public Matrix4x4 getCurrentMatrix() {
        return currentMatrix;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public int getPerFrameDrawCalls() { return perFrameDrawCalls; }

    public void begin() {
        begin(null);
    }

    public void begin(Matrix4x4 combined) {
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D_2.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.perFrameDrawCalls = 0;
        this.currentMatrix = combined != null ? combined : defaultMatrix.setToOrthographicProjection(-GraphicsUtils.getWindowWidth() / 2.0f, GraphicsUtils.getWindowWidth() / 2.0f, -GraphicsUtils.getWindowHeight() / 2.0f, GraphicsUtils.getWindowHeight() / 2.0f, 0, 100);
        setShader(defaultShader);
        setShaderAttributes(null);
        setTexture(whitePixel);
        setMode(GL11.GL_TRIANGLES);
        setTint(WHITE_TINT);
        this.drawing = true;
    }

    /* State */

    public void setShader(Shader shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader == shader) return;
        flush();
        ShaderBinder.bind(shader);
        shader.bindUniform("u_camera_combined", currentMatrix);
        shader.bindUniform("u_texture", currentTexture);
        currentShader = shader;
    }

    public void setTexture(Texture texture) {
        if (texture == null) texture = whitePixel;
        if (currentTexture == texture) return;
        flush();
        currentTexture = texture;
        currentShader.bindUniform("u_texture", currentTexture);
    }

    public void setShaderAttributes(HashMap<String, Object> customAttributes) {
        flush();
        currentShader.bindUniforms(customAttributes);
    }

    private void setMode(final int mode) {
        if (currentMode == mode) return;
        flush();
        this.currentMode = mode;
    }

    public void setBlending(int sFactor, int dFactor) {
        if (currentSFactor == sFactor && currentDFactor == dFactor) return;
        flush();
        this.currentSFactor = sFactor;
        this.currentDFactor = dFactor;
    }

    public void setTint(final Color color) {
        if (color == null) setTint(Color.WHITE.toFloatBits());
        else setTint(color.toFloatBits());
    }

    public void setTint(float tintFloatBits) {
        this.currentTint = tintFloatBits;
    }

    /* Rendering API */

    /* Rendering 2D primitives - Textures */

    public void drawTexture(Texture texture, float x, float y, float angleDeg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE >  verticesBuffer.capacity()) flush();
        if (indicesBuffer.limit() + 6 > indicesBuffer.capacity()) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        float widthHalf  = texture.width  * scaleX * 0.5f;
        float heightHalf = texture.height * scaleY * 0.5f;

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        Vector2 arm2 = vectors2Pool.allocate();
        Vector2 arm3 = vectors2Pool.allocate();

        arm0.x = -widthHalf;
        arm0.y =  heightHalf;
        arm0.rotateDeg(angleDeg);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(angleDeg);

        arm2.x =  widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(angleDeg);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(angleDeg);

        /* put vertices */
        verticesBuffer.put(arm0.x + x).put(arm0.y + y).put(currentTint).put(0).put(0); // V0
        verticesBuffer.put(arm1.x + x).put(arm1.y + y).put(currentTint).put(0).put(1); // V1
        verticesBuffer.put(arm2.x + x).put(arm2.y + y).put(currentTint).put(1).put(1); // V2
        verticesBuffer.put(arm3.x + x).put(arm3.y + y).put(currentTint).put(1).put(0); // V3

        /* put indices */
        int startVertex = this.vertexIndex;
        indicesBuffer.put(startVertex + 0);
        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 3);
        indicesBuffer.put(startVertex + 3);
        indicesBuffer.put(startVertex + 1);
        indicesBuffer.put(startVertex + 2);
        vertexIndex += 4;

        /* free resources */
        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
        vectors2Pool.free(arm2);
        vectors2Pool.free(arm3);
    }

    public void drawTexture_new(Texture texture, float x, float y, float angleDeg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if ((vertexIndex + 4) * VERTEX_SIZE >  verticesBuffer.capacity()) flush();
        if (indicesBuffer.limit() + 6 > indicesBuffer.capacity()) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        float widthHalf  = texture.width  * scaleX * 0.5f;
        float heightHalf = texture.height * scaleY * 0.5f;

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        Vector2 arm2 = vectors2Pool.allocate();
        Vector2 arm3 = vectors2Pool.allocate();

        arm0.x = -widthHalf;
        arm0.y =  heightHalf;
        arm0.rotateDeg(angleDeg);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(angleDeg);

        arm2.x =  widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(angleDeg);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(angleDeg);

        /* put vertices */
        FloatBuffer positions = defaultVertexBuffer.positions;
        positions.put(arm0.x + x).put(arm0.y + y);
        positions.put(arm1.x + x).put(arm1.y + y);
        positions.put(arm2.x + x).put(arm2.y + y);
        positions.put(arm3.x + x).put(arm3.y + y);

        FloatBuffer colors = defaultVertexBuffer.colors;
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        FloatBuffer textCoords = defaultVertexBuffer.textCoords;
        textCoords.put(0).put(0);
        textCoords.put(0).put(1);
        textCoords.put(1).put(1);
        textCoords.put(1).put(0);

        /* put indices */
        int startVertex = this.vertexIndex;
        IntBuffer indices = defaultVertexBuffer.indices;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        vertexIndex += 4;

        /* free resources */
        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
        vectors2Pool.free(arm2);
        vectors2Pool.free(arm3);
    }


    /* Rendering Ops: flush(), end(), deleteAll(), createDefaults...() */

    private void flush() {
        GL30.glBindVertexArray(defaultVertexBuffer.vao);
        defaultVertexBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, defaultVertexBuffer.vboPositions);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, defaultVertexBuffer.positions);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, defaultVertexBuffer.vboColors);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, defaultVertexBuffer.colors);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, defaultVertexBuffer.vboTextCoords);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, defaultVertexBuffer.textCoords);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, defaultVertexBuffer.ebo);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, defaultVertexBuffer.indices);

        GL11.glDrawElements(currentMode, defaultVertexBuffer.indices.limit(), GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        defaultVertexBuffer.clear();
        vertexIndex = 0;
        perFrameDrawCalls++;

        if (true) return;
        if (verticesBuffer.position() == 0) return;
        verticesBuffer.flip();
        indicesBuffer.flip();
        GL30.glBindVertexArray(vao);
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indicesBuffer);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL11.glDrawElements(currentMode, indicesBuffer.limit(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);
        verticesBuffer.clear();
        indicesBuffer.clear();
        vertexIndex = 0;
        perFrameDrawCalls++;
    }

    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D_2.class.getSimpleName() + ".end() without calling " + Renderer2D_2.class.getSimpleName() + ".begin() first.");
        flush();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        currentMatrix = null;
        currentShader = null;
        drawing = false;
    }

    @Override
    public void deleteAll() {
        defaultShader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vbo);
        GL30.glDeleteBuffers(ebo);
        whitePixel.delete();
    }

    /* Create defaults: shader, texture (single white pixel), camera */

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D_2.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D_2.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources. Creating manually.");

            String vertexShader = """
                    #version 450

                    // attributes
                    layout(location = 0) in vec2 a_position;
                    layout(location = 1) in vec4 a_color;
                    layout(location = 2) in vec2 a_textCoords0;

                    // uniforms
                    uniform mat4 u_camera_combined;

                    // outputs
                    out vec4 color;
                    out vec2 uv;

                    void main() {
                        color = a_color;
                        uv = a_textCoords0;
                        gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);
                    };""";

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

            return new Shader(vertexShader, fragmentShader);
        }
    }

    private static Texture createWhiteSinglePixelTexture() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF)); // Red component
        buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));  // Green component
        buffer.put((byte) (0xFF));                      // Blue component
        buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF)); // Alpha component
        buffer.flip();

        return new Texture(1, 1, buffer,
                Texture.Filter.NEAREST, Texture.Filter.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1);
    }

    private static Matrix4x4 createDefaultMatrix() {
        return new Matrix4x4().setToOrthographicProjection(-GraphicsUtils.getWindowWidth() / 2.0f, GraphicsUtils.getWindowWidth() / 2.0f, -GraphicsUtils.getWindowHeight() / 2.0f, GraphicsUtils.getWindowHeight() / 2.0f, 0, 100);
    }

}