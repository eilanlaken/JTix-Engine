package org.example.engine.core.graphics;

import org.example.engine.core.math.*;
import org.example.engine.core.memory.MemoryResourceHolder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Renderer2D implements MemoryResourceHolder {

    private static final int BATCH_SIZE = 4000;
    private static final int VERTEX_SIZE = 5;
    private static final int BATCH_TRIANGLES_CAPACITY = BATCH_SIZE * 2;

    private final ShaderProgram defaultShader = createDefaultShaderProgram();
    private final Texture whiteSinglePixelTexture = createWhiteSinglePixelTexture();
    private final float WHITE_TINT = new Color(1,1,1,1).toFloatBits();
    private final float RED_TINT = new Color(1,0,0,1).toFloatBits();
    private final float GREEN_TINT = new Color(0,1,0,1).toFloatBits();
    private final float BLUE_TINT = new Color(0,0,1,1).toFloatBits();

    private Camera camera;
    private ShaderProgram currentShader;
    private Texture lastTexture;
    private boolean drawing = false;
    private int vertexIndex = 0;
    private int triangleIndex = 0;
    private int mode = GL11.GL_TRIANGLES;

    private final int vao;
    private final int vbo, ebo;
    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(BATCH_SIZE * 4 * VERTEX_SIZE);
    private final IntBuffer indicesBuffer = BufferUtils.createIntBuffer(BATCH_TRIANGLES_CAPACITY * 3);

    // profiling
    private int drawCalls = 0;

    public Renderer2D() {
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        {
            this.vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
            int vertexSizeBytes = VERTEX_SIZE * Float.BYTES;
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, vertexSizeBytes, Float.BYTES * 2L);
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, true, vertexSizeBytes, Float.BYTES * 3L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            this.ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);
        }
        GL30.glBindVertexArray(0);
    }

    public void begin(Camera camera) {
        if (drawing) throw new IllegalStateException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND); // TODO: make camera attributes, get as additional parameter to begin()
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // TODO: make camera attributes, get as additional parameter to begin()
        this.drawCalls = 0;
        this.camera = camera;
        this.currentShader = null;
        drawing = true;
    }

    /** Push primitives: TextureRegion, Shape, Light **/
    public void pushTextureRegion(TextureRegion region, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + 6 > indicesBuffer.limit() || vertexIndex + 20 > BATCH_SIZE * 4) {
            flush();
        }

        final Texture texture = region.texture;
        final float ui = region.u;
        final float vi = region.v;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        if (angleX != 0.0f) scaleX *= MathUtils.cosDeg(angleX);
        if (angleY != 0.0f) scaleY *= MathUtils.cosDeg(angleY);

        useShader(shader);
        useTexture(texture);
        useCustomAttributes(customAttributes);
        useMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 1)
                .put(startVertex + 2)
        ;
        triangleIndex += 6;

        // put vertices
        float localX1, localY1;
        float localX2, localY2;
        float localX3, localY3;
        float localX4, localY4;

        localX1 = localX2 = offsetX - originalWidthHalf;
        localX3 = localX4 = offsetX - originalWidthHalf + packedWidth;
        localY1 = localY4 = offsetY - originalHeightHalf + packedHeight;
        localY2 = localY3 = offsetY - originalHeightHalf;

        if (scaleX != 1.0f) {
            localX1 *= scaleX;
            localX2 *= scaleX;
            localX3 *= scaleX;
            localX4 *= scaleX;
        }
        if (scaleY != 1.0f) {
            localY1 *= scaleY;
            localY2 *= scaleY;
            localY3 *= scaleY;
            localY4 *= scaleY;
        }

        float x1, y1;
        float x2, y2;
        float x3, y3;
        float x4, y4;

        if (angleZ != 0.0f) {
            final float sin = MathUtils.sinDeg(angleZ);
            final float cos = MathUtils.cosDeg(angleZ);
            x1 = localX1 * cos - localY1 * sin;
            y1 = localX1 * sin + localY1 * cos;

            x2 = localX2 * cos - localY2 * sin;
            y2 = localX2 * sin + localY2 * cos;

            x3 = localX3 * cos - localY3 * sin;
            y3 = localX3 * sin + localY3 * cos;

            x4 = localX4 * cos - localY4 * sin;
            y4 = localX4 * sin + localY4 * cos;
        } else {
            x1 = localX1;
            y1 = localY1;

            x2 = localX2;
            y2 = localY2;

            x3 = localX3;
            y3 = localY3;

            x4 = localX4;
            y4 = localY4;
        }

        x1 += x;
        y1 += y;

        x2 += x;
        y2 += y;

        x3 += x;
        y3 += y;

        x4 += x;
        y4 += y;

        float t = tint == null ? WHITE_TINT : tint.toFloatBits();
        verticesBuffer
                .put(x1).put(y1).put(t).put(ui).put(vi) // V1
                .put(x2).put(y2).put(t).put(ui).put(vf) // V2
                .put(x3).put(y3).put(t).put(uf).put(vf) // V3
                .put(x4).put(y4).put(t).put(uf).put(vi) // V4
        ;
        vertexIndex += 20;
    }

    public void pushPolygon(final Shape2DPolygon polygon, Color tint, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY, ShaderProgram shader, HashMap<String, Object> customAttributes) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + polygon.indices.length > indicesBuffer.limit() || vertexIndex + polygon.localPoints.length > BATCH_SIZE * 4) {
            flush();
        }
        useShader(shader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(customAttributes);
        useMode(GL11.GL_TRIANGLES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < polygon.indices.length; i++) {
            indicesBuffer.put(startVertex + polygon.indices[i]);
        }
        triangleIndex += polygon.indices.length;

        if (angleX != 0.0f) scaleX *= MathUtils.cosDeg(angleX);
        if (angleY != 0.0f) scaleY *= MathUtils.cosDeg(angleY);

        polygon.transform(x, y, angleZ, scaleX, scaleY);
        polygon.update();

        float t = tint == null ? WHITE_TINT : tint.toFloatBits();
        final float[] worldPoints = polygon.getWorldPoints();

        for (int i = 0; i < worldPoints.length - 1; i += 2) {
            verticesBuffer.put(worldPoints[i]).put(worldPoints[i+1]).put(t).put(0.5f).put(0.5f);
        }
        vertexIndex += polygon.vertexCount * 5;
    }

    public void pushLight() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void pushDebugShape(Shape2D shape, final Color tint) {
        final float tintFloatBits = tint == null ? BLUE_TINT : tint.toFloatBits();
        pushDebugShape(shape, tintFloatBits);
    }

    public void pushDebugShape(Shape2D shape, final float tintFloatBits) {
        if (shape instanceof Shape2DCircle) pushDebugCircle((Shape2DCircle) shape, tintFloatBits);
        if (shape instanceof Shape2DRectangle) pushDebugRectangle((Shape2DRectangle) shape, tintFloatBits);
        if (shape instanceof Shape2DAABB) pushDebugAABB((Shape2DAABB) shape, tintFloatBits);
        if (shape instanceof Shape2DSegment) pushDebugSegment((Shape2DSegment) shape, tintFloatBits);
        if (shape instanceof Shape2DPolygon) pushDebugPolygon((Shape2DPolygon) shape, tintFloatBits);
        if (shape instanceof Shape2DMorphed) pushDebugCompoundShape((Shape2DMorphed) shape, tintFloatBits);
    }

    private void pushDebugCircle(final Shape2DCircle circle, final float tintFloatBits) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + 34 > indicesBuffer.limit() || vertexIndex + 17 * 5 > BATCH_SIZE * 4) {
            System.out.println("flashing: " + drawCalls);
            flush();
        }

        useShader(defaultShader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(null);
        useMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 1; i < 15; i++) {
            indicesBuffer.put(startVertex + i - 1);
            indicesBuffer.put(startVertex + i);
        }
        indicesBuffer.put(startVertex + 14);
        indicesBuffer.put(startVertex);
        indicesBuffer.put(startVertex + 15);
        indicesBuffer.put(startVertex + 16);
        triangleIndex += 34;

        circle.update();
        float x = circle.worldCenter.x;
        float y = circle.worldCenter.y;
        float r = circle.worldRadius;
        float da = 360f / 15;
        for (int i = 0; i < 15; i++) {
            verticesBuffer
                    .put(x + r * MathUtils.cosDeg(da * i))
                    .put(y + r * MathUtils.sinDeg(da * i))
                    .put(tintFloatBits)
                    .put(0.5f)
                    .put(0.5f)
            ;
        }
        verticesBuffer.put(x).put(y).put(tintFloatBits).put(0.5f).put(0.5f);
        verticesBuffer.put(x + r * MathUtils.cosDeg(circle.angle())).put(y + r * MathUtils.sinDeg(circle.angle())).put(tintFloatBits).put(0.5f).put(0.5f);
        vertexIndex += 17 * 5;
    }

    private void pushDebugRectangle(final Shape2DRectangle rectangle, final float tintFloatBits) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + 10 > indicesBuffer.limit() || vertexIndex + 6 * 5 > BATCH_SIZE * 4) {
            flush();
        }

        useShader(defaultShader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(null);
        useMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex)
        ;
        indicesBuffer.put(startVertex + 4);
        indicesBuffer.put(startVertex + 5);
        triangleIndex += 10;

        rectangle.update();
        float x1 = rectangle.c1().x, y1 = rectangle.c1().y;
        float x2 = rectangle.c2().x, y2 = rectangle.c2().y;
        float x3 = rectangle.c3().x, y3 = rectangle.c3().y;
        float x4 = rectangle.c4().x, y4 = rectangle.c4().y;
        verticesBuffer
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // V1
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // V2
                .put(x3).put(y3).put(tintFloatBits).put(0.5f).put(0.5f) // V3
                .put(x4).put(y4).put(tintFloatBits).put(0.5f).put(0.5f) // V4
        ;

        float centerX = (x1 + x2 + x3 + x4) * 0.25f;
        float centerY = (y1 + y2 + y3 + y4) * 0.25f;
        float lineEndX = (x3 + x4) * 0.5f;
        float lineEndY = (y3 + y4) * 0.5f;
        verticesBuffer.put(centerX).put(centerY).put(tintFloatBits).put(0.5f).put(0.5f);
        verticesBuffer.put(lineEndX).put(lineEndY).put(tintFloatBits).put(0.5f).put(0.5f);
        vertexIndex += 6 * 5;
    }

    private void pushDebugAABB(final Shape2DAABB aabb, final float tintFloatBits) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + 10 > indicesBuffer.limit() || vertexIndex + 6 * 5 > BATCH_SIZE * 4) {
            flush();
        }

        useShader(defaultShader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(null);
        useMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex)
        ;
        indicesBuffer.put(startVertex + 4);
        indicesBuffer.put(startVertex + 5);
        triangleIndex += 10;

        aabb.update();
        float x1 = aabb.worldMin.x, y1 = aabb.worldMax.y;
        float x2 = aabb.worldMin.x, y2 = aabb.worldMin.y;
        float x3 = aabb.worldMax.x, y3 = aabb.worldMin.y;
        float x4 = aabb.worldMax.x, y4 = aabb.worldMax.y;
        verticesBuffer
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // V1
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // V2
                .put(x3).put(y3).put(tintFloatBits).put(0.5f).put(0.5f) // V3
                .put(x4).put(y4).put(tintFloatBits).put(0.5f).put(0.5f) // V4
        ;

        float centerX = (x1 + x2 + x3 + x4) * 0.25f;
        float centerY = (y1 + y2 + y3 + y4) * 0.25f;
        float lineEndX = (x3 + x4) * 0.5f;
        float lineEndY = (y3 + y4) * 0.5f;
        verticesBuffer.put(centerX).put(centerY).put(tintFloatBits).put(0.5f).put(0.5f);
        verticesBuffer.put(lineEndX).put(lineEndY).put(tintFloatBits).put(0.5f).put(0.5f);
        vertexIndex += 6 * 5;
    }

    private void pushDebugSegment(final Shape2DSegment segment, final float tintFloatBits) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + 2 > indicesBuffer.limit() || vertexIndex + 2 * 5 > BATCH_SIZE * 4) {
            flush();
        }

        useShader(defaultShader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(null);
        useMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        indicesBuffer
                .put(startVertex)
                .put(startVertex + 1)
        ;
        triangleIndex += 2;

        segment.update();
        float x1 = segment.world_a.x, y1 = segment.world_a.y;
        float x2 = segment.world_b.x, y2 = segment.world_b.y;
        verticesBuffer
                .put(x1).put(y1).put(tintFloatBits).put(0.5f).put(0.5f) // a
                .put(x2).put(y2).put(tintFloatBits).put(0.5f).put(0.5f) // b
        ;
        vertexIndex += 2 * 5;
    }

    private void pushDebugPolygon(final Shape2DPolygon polygon, final float tintFloatBits) {
        if (!drawing) throw new IllegalStateException("Must call begin() before draw operations.");
        if (triangleIndex + polygon.indices.length * 2 + 2 > indicesBuffer.limit() || vertexIndex + polygon.vertexCount * 5 > BATCH_SIZE * 4) {
            flush();
        }

        useShader(defaultShader);
        useTexture(whiteSinglePixelTexture);
        useCustomAttributes(null);
        useMode(GL11.GL_LINES);

        // put indices
        int startVertex = this.vertexIndex / VERTEX_SIZE;
        for (int i = 0; i < polygon.indices.length - 2; i += 3) {
            indicesBuffer.put(startVertex + polygon.indices[i]);
            indicesBuffer.put(startVertex + polygon.indices[i + 1]);

            indicesBuffer.put(startVertex + polygon.indices[i + 1]);
            indicesBuffer.put(startVertex + polygon.indices[i + 2]);

            indicesBuffer.put(startVertex + polygon.indices[i + 2]);
            indicesBuffer.put(startVertex + polygon.indices[i]);
        }
        triangleIndex += polygon.indices.length * 2;

        polygon.update();
        final float[] worldPoints = polygon.getWorldPoints();
        for (int i = 0; i < worldPoints.length - 1; i += 2) {
            verticesBuffer.put(worldPoints[i]).put(worldPoints[i+1]).put(tintFloatBits).put(0.5f).put(0.5f);
        }
        vertexIndex += polygon.vertexCount * 5;
    }

    private void pushDebugCompoundShape(final Shape2DMorphed compound, final float tintFloatBits) {
        for (Shape2D island : compound.islands) {
            pushDebugShape(island, tintFloatBits);
        }
        for (Shape2D hole : compound.holes) {
            pushDebugShape(hole, RED_TINT);
        }
    }

    /** Swap Operations **/
    private void useShader(ShaderProgram shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader != shader) {
            flush();
            ShaderProgramBinder.bind(shader);
            shader.bindUniform("u_camera_combined", camera.lens.combined);
        }
        currentShader = shader;
    }

    private void useTexture(Texture texture) {
        if (lastTexture != texture) {
            flush();
        }
        lastTexture = texture;
        currentShader.bindUniform("u_texture", lastTexture);
    }

    // TODO: unify with shader switching?
    @Deprecated private void useCustomAttributes(HashMap<String, Object> customAttributes) {
        if (customAttributes != null) {
            flush();
            currentShader.bindUniforms(customAttributes);
        }
    }

    private void useMode(final int mode) {
        if (mode != this.mode) {
            System.out.println("change draw mode");
            flush();
        }
        this.mode = mode;
    }

    // contains the logic that sends everything to the GPU for rendering
    private void flush() {
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
            GL11.glDrawElements(mode, indicesBuffer.limit(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);

        verticesBuffer.clear();
        indicesBuffer.clear();
        vertexIndex = 0;
        triangleIndex = 0;
        drawCalls++;
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
        flush();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        drawing = false;
    }

    public int getDrawCalls() {
        return drawCalls;
    }

    @Override
    public void deleteAll() {
        defaultShader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vbo);
        GL30.glDeleteBuffers(ebo);
        whiteSinglePixelTexture.delete();
    }

    private static ShaderProgram createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new ShaderProgram(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources: ");
            e.printStackTrace();
            String vertexShader = "#version 450\n" +
                    "\n" +
                    "// attributes\n" +
                    "layout(location = 0) in vec2 a_position;\n" +
                    "layout(location = 1) in vec4 a_color;\n" +
                    "layout(location = 2) in vec2 a_texCoord0;\n" +
                    "\n" +
                    "// uniforms\n" +
                    "uniform mat4 u_camera_combined;\n" +
                    "\n" +
                    "// outputs\n" +
                    "out vec4 color;\n" +
                    "out vec2 uv;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    color = a_color;\n" +
                    "    uv = a_texCoord0;\n" +
                    "    gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);\n" +
                    "};";

            String fragmentShader = "#version 450\n" +
                    "\n" +
                    "// inputs\n" +
                    "in vec4 color;\n" +
                    "in vec2 uv;\n" +
                    "\n" +
                    "// uniforms\n" +
                    "uniform sampler2D u_texture;\n" +
                    "\n" +
                    "// outputs\n" +
                    "layout (location = 0) out vec4 out_color;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    out_color = color * texture(u_texture, uv);\n" +
                    "}";

            return new ShaderProgram(vertexShader, fragmentShader);
        }
    }

    private static Texture createWhiteSinglePixelTexture() {
        try {
            return TextureBuilder.buildFromClassPath("graphics-2d-single-white-pixel.png");
        } catch (Exception e) {
            System.err.println("Could not create single white pixel texture from resource. Creating manually.");
            e.printStackTrace();

            ByteBuffer buffer = ByteBuffer.allocateDirect(4);
            buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF));   // Red component
            buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));    // Green component
            buffer.put((byte) (0xFF));           // Blue component
            buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF));   // Alpha component
            buffer.flip();
            int glHandle = GL11.glGenTextures();
            Texture texture = new Texture(glHandle,
                    1, 1,
                    Texture.Filter.NEAREST, Texture.Filter.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE
            );
            TextureBinder.bind(texture);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            return texture;
        }
    }

}
