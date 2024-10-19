package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.memory.MemoryResourceHolder;
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
import java.util.stream.Collectors;

public class Renderer2D_3 implements MemoryResourceHolder {

    private static final int   VERTICES_CAPACITY = 8000; // The batch can render VERTICES_CAPACITY vertices (so wee need a float buffer of size: VERTICES_CAPACITY * VERTEX_SIZE)
    private static final float WHITE_TINT        = Color.WHITE.toFloatBits();

    /* defaults */
    private final Shader    defaultShader = createDefaultShaderProgram();
    private final Texture   whitePixel    = createWhiteSinglePixelTexture();
    private final Matrix4x4 defaultMatrix = createDefaultMatrix();

    /* memory pools */
    private final MemoryPool<Vector2>    vectors2Pool   = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);

    /* state */
    private Matrix4x4 currentMatrix     = defaultMatrix;
    private Texture   currentTexture    = whitePixel;
    private Shader    currentShader     = null;
    private float     currentTint       = WHITE_TINT;
    private boolean   drawing           = false;
    private int       vertexIndex       = 0;
    private int       currentMode       = GL11.GL_TRIANGLES;
    private int       currentSFactor    = GL11.GL_SRC_ALPHA;
    private int       currentDFactor    = GL11.GL_ONE_MINUS_SRC_ALPHA;
    private int       perFrameDrawCalls = 0;

    /* Vertex Buffers */
    private final int         vao;
    private final int         vboPositions;
    private final int         vboColors;
    private final int         vboTextCoords;
    private final int         vboNormals;
    private final int         vboTangents;
    private final int         ebo;
    private final FloatBuffer positions;
    private final FloatBuffer colors;
    private final FloatBuffer textCoords;
    private final FloatBuffer normals;
    private final FloatBuffer tangents;
    private final IntBuffer   indices;

    public Renderer2D_3() {
        positions  = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        colors     = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 1);
        textCoords = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        normals    = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        tangents   = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        indices    = BufferUtils.createIntBuffer(VERTICES_CAPACITY);

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        this.vboPositions = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        //GL20.glEnableVertexAttribArray(0);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.vboColors = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);
        //GL20.glEnableVertexAttribArray(1);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.vboTextCoords = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextCoords); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoords, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
        //GL20.glEnableVertexAttribArray(2);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.vboNormals = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboNormals); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normals, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, 0, 0);
        //GL20.glEnableVertexAttribArray(3);

        this.vboTangents = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTangents); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tangents, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(4, 2, GL11.GL_FLOAT, false, 0, 0);
        //GL20.glEnableVertexAttribArray(4);

        this.ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

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
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D_3.class.getSimpleName() + ".end() before calling begin().");
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
        if (!ensureCapacity(4)) flush();

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
        positions.put(arm0.x + x).put(arm0.y + y);
        positions.put(arm1.x + x).put(arm1.y + y);
        positions.put(arm2.x + x).put(arm2.y + y);
        positions.put(arm3.x + x).put(arm3.y + y);

        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0).put(0);
        textCoords.put(0).put(1);
        textCoords.put(1).put(1);
        textCoords.put(1).put(0);

        /* put indices */
        int startVertex = this.vertexIndex;
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

    public final void drawLineThin(float x1, float y1, float x2, float y2) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(whitePixel);

        positions.put(x1).put(y1);
        positions.put(x2).put(y2);

        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);
        vertexIndex += 2;
    }

    /* Rendering Ops: flush(), end(), deleteAll(), createDefaults...() */

    private boolean ensureCapacity(int vertices) {
        return true; // TODO
    }

    private void flush() {
        if (vertexIndex == 0) return;

        GL30.glBindVertexArray(vao);
        positions.flip();
        colors.flip();
        textCoords.flip();
        normals.flip();
        tangents.flip();
        indices.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positions);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, colors);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextCoords);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, textCoords);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indices);

        for (VertexAttribute attribute : VertexAttribute.values()) {
            final boolean hasAttribute = (currentShader.vertexAttributesBitmask & attribute.bitmask) != 0;
            if (hasAttribute) GL20.glEnableVertexAttribArray(attribute.glslLocation); // enable attribute
            else GL20.glDisableVertexAttribArray(attribute.glslLocation); // disable attribute
        }
        GL11.glDrawElements(currentMode, indices.limit(), GL11.GL_UNSIGNED_INT, 0);


        GL30.glBindVertexArray(0);
        positions.clear();
        colors.clear();
        textCoords.clear();
        normals.clear();
        tangents.clear();
        indices.clear();
        vertexIndex = 0;
        perFrameDrawCalls++;
    }

    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D_3.class.getSimpleName() + ".end() without calling " + Renderer2D_3.class.getSimpleName() + ".begin() first.");
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
        GL30.glDeleteBuffers(vboPositions);
        GL30.glDeleteBuffers(vboColors);
        GL30.glDeleteBuffers(vboTextCoords);
        GL30.glDeleteBuffers(vboNormals);
        GL30.glDeleteBuffers(vboTangents);
        GL30.glDeleteBuffers(ebo);
        whitePixel.delete();
    }

    /* Create defaults: shader, texture (single white pixel), camera */

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D_3.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D_3.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
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