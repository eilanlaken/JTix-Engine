package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Renderer2D implements MemoryResourceHolder {

    private static final int   VERTICES_CAPACITY = 8000; // The batch can render VERTICES_CAPACITY vertices (so wee need a float buffer of size: VERTICES_CAPACITY * VERTEX_SIZE)
    private static final float WHITE_TINT        = Color.WHITE.toFloatBits();

    /* defaults */
    private final Shader    defaultShader  = createDefaultShaderProgram();
    private final Texture   defaultTexture = createDefaultTexture();
    private final Matrix4x4 defaultMatrix  = createDefaultMatrix();
    private final Font      defaultFont    = createDefaultFont();

    /* memory pools */
    private final MemoryPool<Vector2>    vectors2Pool   = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);

    /* state */
    private Matrix4x4 currentMatrix     = defaultMatrix;
    private Texture   currentTexture    = defaultTexture;
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

    public Renderer2D() {
        positions  = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        colors     = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 1);
        textCoords = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        normals    = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        tangents   = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        indices    = BufferUtils.createIntBuffer(VERTICES_CAPACITY * 2);

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
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.perFrameDrawCalls = 0;
        this.currentMatrix = combined != null ? combined : defaultMatrix.setToOrthographicProjection(-Graphics.getWindowWidth() / 2.0f, Graphics.getWindowWidth() / 2.0f, -Graphics.getWindowHeight() / 2.0f, Graphics.getWindowHeight() / 2.0f, 0, 100);
        setShader(defaultShader);
        setShaderAttributes(null);
        setTexture(defaultTexture);
        setMode(GL11.GL_TRIANGLES);
        setColor(WHITE_TINT);
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
        if (texture == null) texture = defaultTexture;
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

    public void setColor(final Color color) {
        if (color == null) setColor(Color.WHITE.toFloatBits());
        else setColor(color.toFloatBits());
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(Color.toFloatBits(r,g,b,a));
    }

    public void setColor(float tintFloatBits) {
        this.currentTint = tintFloatBits;
    }

    /* Rendering API */

    /* Rendering 2D primitives - Textures */

    public void drawTexture(Texture texture, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

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
        arm0.rotateDeg(degrees);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(degrees);

        arm2.x =  widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(degrees);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(degrees);

        /* put vertices */
        positions.put(arm0.x + x).put(arm0.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(0);

        positions.put(arm1.x + x).put(arm1.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(1);

        positions.put(arm2.x + x).put(arm2.y + y);
        colors.put(currentTint);
        textCoords.put(1).put(1);

        positions.put(arm3.x + x).put(arm3.y + y);
        colors.put(currentTint);
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


    public void drawTexture(@NotNull Texture texture, float cornerRadiusPixels, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if (!ensureCapacity(refinement * 4, 3 * (refinement * 4 - 2))) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(texture);

        float widthHalf  = texture.width  * 0.5f;
        float heightHalf = texture.height * 0.5f;
        float da = 90.0f / (refinement - 1);

        Vector2 corner = vectors2Pool.allocate();
        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadiusPixels, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusPixels,heightHalf - cornerRadiusPixels);
            float u = (corner.x + widthHalf) / texture.width;
            float v = 1 - (corner.y + heightHalf) / texture.height;
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(u).put(v);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadiusPixels);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusPixels, heightHalf - cornerRadiusPixels);
            float u = (corner.x + widthHalf) / texture.width;
            float v = 1 - (corner.y + heightHalf) / texture.height;
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(u).put(v);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadiusPixels, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusPixels, -heightHalf + cornerRadiusPixels);
            float u = (corner.x + widthHalf) / texture.width;
            float v = 1 - (corner.y + heightHalf) / texture.height;
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(u).put(v);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadiusPixels);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusPixels, -heightHalf + cornerRadiusPixels);
            float u = (corner.x + widthHalf) / texture.width;
            float v = 1 - (corner.y + heightHalf) / texture.height;
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(u).put(v);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement * 4 - 2; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }

        vectors2Pool.free(corner);
        vertexIndex += refinement * 4;
    }


    public void drawTexture(Texture texture, float u1, float v1, float u2, float v2, float x, float y, float angleX, float angleY, float angleZ, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        float width  = texture.width  * scaleX * MathUtils.cosDeg(angleY);
        float height = texture.height * scaleY * MathUtils.cosDeg(angleX);
        float widthHalf  = width * 0.5f;
        float heightHalf = height * 0.5f;

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        Vector2 arm2 = vectors2Pool.allocate();
        Vector2 arm3 = vectors2Pool.allocate();

        arm0.x = -widthHalf + width * u1;
        arm0.y =  heightHalf - height * v1;
        arm0.rotateDeg(angleZ);

        arm1.x = -widthHalf + width * u1;
        arm1.y = -heightHalf + height * (1 - v2);
        arm1.rotateDeg(angleZ);

        arm2.x =  widthHalf - width * (1 - u2);
        arm2.y = -heightHalf + height * (1 - v2);
        arm2.rotateDeg(angleZ);

        arm3.x = widthHalf - width * (1 - u2);
        arm3.y = heightHalf - height * v1;
        arm3.rotateDeg(angleZ);

        /* put vertices */
        positions.put(arm0.x + x).put(arm0.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v1);

        positions.put(arm1.x + x).put(arm1.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v2);

        positions.put(arm2.x + x).put(arm2.y + y);
        colors.put(currentTint);
        textCoords.put(u2).put(v2);

        positions.put(arm3.x + x).put(arm3.y + y);
        colors.put(currentTint);
        textCoords.put(u2).put(v1);

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

    public void drawTextureRegion(TexturePack.Region region, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setTexture(region.texture);
        setMode(GL11.GL_TRIANGLES);

        final float ui = region.u1;
        final float vi = region.v1;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        float localX1 = offsetX - originalWidthHalf;
        float localX2 = offsetX - originalWidthHalf;
        float localX3 = offsetX - originalWidthHalf + packedWidth;
        float localX4 = offsetX - originalWidthHalf + packedWidth;
        float localY1 = offsetY - originalHeightHalf + packedHeight;
        float localY4 = offsetY - originalHeightHalf + packedHeight;
        float localY2 = offsetY - originalHeightHalf;
        float localY3 = offsetY - originalHeightHalf;

        /* apply scale */
        localX1 *= scaleX;
        localX2 *= scaleX;
        localX3 *= scaleX;
        localX4 *= scaleX;
        localY1 *= scaleY;
        localY2 *= scaleY;
        localY3 *= scaleY;
        localY4 *= scaleY;

        /* apply rotation */
        final float sin = MathUtils.sinDeg(degrees);
        final float cos = MathUtils.cosDeg(degrees);
        float x1 = localX1 * cos - localY1 * sin;
        float y1 = localX1 * sin + localY1 * cos;
        float x2 = localX2 * cos - localY2 * sin;
        float y2 = localX2 * sin + localY2 * cos;
        float x3 = localX3 * cos - localY3 * sin;
        float y3 = localX3 * sin + localY3 * cos;
        float x4 = localX4 * cos - localY4 * sin;
        float y4 = localX4 * sin + localY4 * cos;

        /* apply translation */
        x1 += x;
        y1 += y;
        x2 += x;
        y2 += y;
        x3 += x;
        y3 += y;
        x4 += x;
        y4 += y;

        /* put vertices */
        positions.put(x1).put(y1);
        colors.put(currentTint);
        textCoords.put(ui).put(vi);

        positions.put(x2).put(y2);
        colors.put(currentTint);
        textCoords.put(ui).put(vf);

        positions.put(x3).put(y3);
        colors.put(currentTint);
        textCoords.put(uf).put(vf);

        positions.put(x4).put(y4);
        colors.put(currentTint);
        textCoords.put(uf).put(vi);

        /* put indices */
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        vertexIndex += 4;
    }

    /* Rendering 2D primitives - circles */

    public void drawCircleThin(float r, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);
        if (!ensureCapacity(refinement, 2 * (refinement + 1))) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        Vector2 arm = vectors2Pool.allocate();
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            arm.x = x + r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = y + r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(degrees);
            positions.put(arm.x).put(arm.y);
            textCoords.put(0.5f).put(0.5f);
            colors.put(currentTint);
        }
        vectors2Pool.free(arm);

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 1; i < refinement; i++) {
            indices.put(startVertex + i - 1);
            indices.put(startVertex + i);
        }
        indices.put(startVertex + refinement - 1);
        indices.put(startVertex);

        vertexIndex += refinement;
    }

    public void drawCircleFilled(float r, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);
        if (!ensureCapacity(refinement, 3 * (refinement - 2))) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 arm = vectors2Pool.allocate();
        float da = 360f / refinement;

        /* put vertices */
        for (int i = 0; i < refinement; i++) {
            arm.x = r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(degrees);
            float pointX = x + arm.x;
            float pointY = y + arm.y;
            positions.put(pointX).put(pointY);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement - 2; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }
        vertexIndex += refinement;

        vectors2Pool.free(arm);
    }

    public void drawCircleFilled(float r, int refinement, float angle, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(refinement, 3);
        if (!ensureCapacity(refinement, 3 * (refinement - 2))) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 arm = vectors2Pool.allocate();
        positions.put(x).put(y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);
        float da = angle / refinement;

        for (int i = 0; i < refinement + 1; i++) {
            arm.x = r * scaleX * MathUtils.cosDeg(da * i);
            arm.y = r * scaleY * MathUtils.sinDeg(da * i);
            arm.rotateDeg(degrees);
            float pointX = x + arm.x;
            float pointY = y + arm.y;
            positions.put(pointX).put(pointY);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }
        vectors2Pool.free(arm);

        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }

        vertexIndex += refinement + 2;
    }

    public void drawCircleBorder(float r, float thickness, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(3, refinement);
        if (!ensureCapacity(refinement * 2, refinement * 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        float da = 360f / refinement;
        float halfBorder = thickness * 0.5f;

        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;

            arm0.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            arm0.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            arm0.rotateDeg(degrees);

            arm1.x = scaleX * (r + halfBorder) * MathUtils.cosDeg(currentAngle);
            arm1.y = scaleY * (r + halfBorder) * MathUtils.sinDeg(currentAngle);
            arm1.rotateDeg(degrees);

            positions.put(arm0.x + x).put(arm0.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            positions.put(arm1.x + x).put(arm1.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < (refinement - 1) * 2; i += 2) { // 012 213
            indices.put(startVertex + i + 0);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
            indices.put(startVertex + i + 2);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 3);
        }
        indices.put(startVertex + refinement * 2 - 2);
        indices.put(startVertex + refinement * 2 - 1);
        indices.put(startVertex + 0);
        indices.put(startVertex + 0);
        indices.put(startVertex + refinement * 2 - 1);
        indices.put(startVertex + 1);
        vertexIndex += refinement * 2;

        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
    }

    public void drawCircleBorder(float r, float thickness, float angle, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(3, refinement);
        if (!ensureCapacity(refinement * 2, refinement * 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        float da = angle / refinement;
        float halfBorder = thickness * 0.5f;
        // render arc segments.
        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;

            arm0.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            arm0.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            arm0.rotateDeg(degrees);

            arm1.x = scaleX * (r + halfBorder) * (MathUtils.cosDeg(currentAngle));
            arm1.y = scaleY * (r + halfBorder) * (MathUtils.sinDeg(currentAngle));
            arm1.rotateDeg(degrees);

            positions.put(arm0.x + x).put(arm0.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            positions.put(arm1.x + x).put(arm1.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < (refinement - 1) * 2; i += 2) { // 012 213
            indices.put(startVertex + i + 0);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
            indices.put(startVertex + i + 2);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 3);
        }
        vertexIndex += refinement * 2;

        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
    }

    /* Rendering 2D primitives - Rectangles */
    // TODO: delete. This is not necessarily a rectangle. This should be called from physics so simply replace the call.
    @Deprecated public void drawRectangleThin(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 0);

        positions.put(x0).put(y0);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x1).put(y1);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x2).put(y2);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x3).put(y3);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        vertexIndex += 4;
    }

    public void drawRectangleThin(float width, float height, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        Vector2 arm2 = vectors2Pool.allocate();
        Vector2 arm3 = vectors2Pool.allocate();

        float widthHalf  = width  * scaleX * 0.5f;
        float heightHalf = height * scaleY * 0.5f;

        arm0.x = -widthHalf;
        arm0.y = heightHalf;
        arm0.rotateDeg(degrees);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(degrees);

        arm2.x = widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(degrees);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(degrees);

        positions.put(arm0.x + x).put(arm0.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(arm1.x + x).put(arm1.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(arm2.x + x).put(arm2.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(arm3.x + x).put(arm3.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 0);
        vertexIndex += 4;

        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
        vectors2Pool.free(arm2);
        vectors2Pool.free(arm3);
    }

    public void drawRectangleThin(float width, float height, float cornerRadius, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if (!ensureCapacity(refinement * 4, refinement * 8)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);

        Vector2 corner = vectors2Pool.allocate();

        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement * 4; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + (i + 1) % (refinement*4));
        }

        vectors2Pool.free(corner);
        vertexIndex += refinement * 4;
    }

    public void drawRectangleFilled(float width, float height, float x, float y, float degrees, float scaleX, float scaleY) {
        drawRectangleFilled(null, width, height, x, y, degrees, scaleX, scaleY);
    }

    public void drawRectangleFilled(@Nullable Texture texture, float width, float height, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4,6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(texture);

        float widthHalf  = width  * scaleX * 0.5f;
        float heightHalf = height * scaleY * 0.5f;

        Vector2 arm0 = vectors2Pool.allocate();
        Vector2 arm1 = vectors2Pool.allocate();
        Vector2 arm2 = vectors2Pool.allocate();
        Vector2 arm3 = vectors2Pool.allocate();

        arm0.x = -widthHalf;
        arm0.y =  heightHalf;
        arm0.rotateDeg(degrees);

        arm1.x = -widthHalf;
        arm1.y = -heightHalf;
        arm1.rotateDeg(degrees);

        arm2.x =  widthHalf;
        arm2.y = -heightHalf;
        arm2.rotateDeg(degrees);

        arm3.x = widthHalf;
        arm3.y = heightHalf;
        arm3.rotateDeg(degrees);

        positions.put(arm0.x + x).put(arm0.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(0);

        positions.put(arm1.x + x).put(arm1.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(1);

        positions.put(arm2.x + x).put(arm2.y + y);
        colors.put(currentTint);
        textCoords.put(1).put(1);

        positions.put(arm3.x + x).put(arm3.y + y);
        colors.put(currentTint);
        textCoords.put(1).put(0);

        /* put indices */
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        indices.put(startVertex + 0);
        vertexIndex += 4;

        /* free resources */
        vectors2Pool.free(arm0);
        vectors2Pool.free(arm1);
        vectors2Pool.free(arm2);
        vectors2Pool.free(arm3);
    }

    public void drawRectangleFilled(float width, float height, float cornerRadius, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if (!ensureCapacity(refinement * 4, refinement * 12)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float widthHalf  = width  * scaleX * 0.5f;
        float heightHalf = height * scaleY * 0.5f;
        float da = 90.0f / (refinement - 1);

        Vector2 corner = vectors2Pool.allocate();
        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius,heightHalf - cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            corner.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(corner.x).put(corner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement * 4 - 2; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }

        vectors2Pool.free(corner);
        vertexIndex += refinement * 4;
    }

    public void drawRectangleBorder(float width, float height, float thickness, float x, float y, float angleDeg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(8, 24)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float widthHalf     = width     * 0.5f;
        float heightHalf    = height    * 0.5f;
        float thicknessHalf = thickness * 0.5f;

        Array<Vector2> vertices = new Array<>(true, 8);
        // inner vertices
        Vector2 inner_vertex_0 = vectors2Pool.allocate().set(-widthHalf + thicknessHalf, heightHalf - thicknessHalf);
        Vector2 inner_vertex_1 = vectors2Pool.allocate().set(-widthHalf + thicknessHalf, -heightHalf + thicknessHalf);
        Vector2 inner_vertex_2 = vectors2Pool.allocate().set(widthHalf - thicknessHalf, -heightHalf + thicknessHalf);
        Vector2 inner_vertex_3 = vectors2Pool.allocate().set(widthHalf - thicknessHalf, heightHalf - thicknessHalf);
        // outer vertices
        Vector2 outer_vertex_0 = vectors2Pool.allocate().set(-widthHalf - thicknessHalf, heightHalf + thicknessHalf);
        Vector2 outer_vertex_1 = vectors2Pool.allocate().set(-widthHalf - thicknessHalf, -heightHalf - thicknessHalf);
        Vector2 outer_vertex_2 = vectors2Pool.allocate().set(widthHalf + thicknessHalf, -heightHalf - thicknessHalf);
        Vector2 outer_vertex_3 = vectors2Pool.allocate().set(widthHalf + thicknessHalf, heightHalf + thicknessHalf);

        vertices.add(inner_vertex_0, inner_vertex_1, inner_vertex_2, inner_vertex_3);
        vertices.add(outer_vertex_0, outer_vertex_1, outer_vertex_2, outer_vertex_3);

        // transform each vertex, then put it in the buffer + tint + uv
        for (int i = 0; i < vertices.size; i++) {
            Vector2 vertex = vertices.get(i);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(angleDeg);
            vertex.add(x, y);
            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 4);
        indices.put(startVertex + 5);
        indices.put(startVertex + 0);
        indices.put(startVertex + 5);
        indices.put(startVertex + 1);
        indices.put(startVertex + 1);
        indices.put(startVertex + 5);
        indices.put(startVertex + 6);
        indices.put(startVertex + 1);
        indices.put(startVertex + 6);
        indices.put(startVertex + 2);
        indices.put(startVertex + 2);
        indices.put(startVertex + 6);
        indices.put(startVertex + 7);
        indices.put(startVertex + 2);
        indices.put(startVertex + 7);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 7);
        indices.put(startVertex + 4);
        indices.put(startVertex + 3);
        indices.put(startVertex + 4);
        indices.put(startVertex + 0);
        vertexIndex += 8;

        vectors2Pool.freeAll(vertices);
    }

    /* Rendering 2D primitives - Polygons */

    public void drawPolygonThin(float[] polygon, boolean triangulated, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (!ensureCapacity(count, count * 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        int startVertex = this.vertexIndex;
        if (!triangulated) {
            Vector2 vertex = vectors2Pool.allocate();
            for (int i = 0; i < polygon.length; i += 2) {
                float poly_x = polygon[i];
                float poly_y = polygon[i + 1];
                vertex.set(poly_x, poly_y);
                vertex.scl(scaleX, scaleY);
                vertex.rotateDeg(degrees);
                vertex.add(x, y);
                positions.put(vertex.x).put(vertex.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
            }
            vectors2Pool.free(vertex);

            for (int i = 0; i < count - 1; i++) {
                indices.put(startVertex + i);
                indices.put(startVertex + i + 1);
            }
            indices.put(startVertex + count - 1);
            indices.put(startVertex + 0);
            vertexIndex += count;
        } else {
            ArrayFloat vertices  = arrayFloatPool.allocate();
            ArrayInt   triangles = arrayIntPool.allocate();
            /* try to triangulate the polygon. We might have a polygon that is degenerate and the triangulation fails. In that case, it is okay to not render anything.*/
            try {
                MathUtils.polygonTriangulate(polygon, vertices, triangles);
            } catch (Exception e) {
                /* Probably the polygon has collapsed into a single point. */
                return;
            }

            Vector2 vertex = vectors2Pool.allocate();
            for (int i = 0; i < vertices.size; i += 2) {
                float poly_x = vertices.get(i);
                float poly_y = vertices.get(i + 1);

                vertex.set(poly_x, poly_y);
                vertex.scl(scaleX, scaleY);
                vertex.rotateDeg(degrees);
                vertex.add(x, y);

                positions.put(vertex.x).put(vertex.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
            }
            vectors2Pool.free(vertex);

            for (int i = 0; i < triangles.size - 2; i += 3) {
                indices.put(startVertex + triangles.get(i));
                indices.put(startVertex + triangles.get(i + 1));

                indices.put(startVertex + triangles.get(i + 1));
                indices.put(startVertex + triangles.get(i + 2));

                indices.put(startVertex + triangles.get(i + 2));
                indices.put(startVertex + triangles.get(i));
            }

            vertexIndex += vertices.size / 2;

            arrayFloatPool.free(vertices);
            arrayIntPool.free(triangles);
        }
    }

    public void drawPolygonThin(float[] polygon, int[] triangles, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (!ensureCapacity(count, count * 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        Vector2 vertex = vectors2Pool.allocate();
        for (int i = 0; i < polygon.length; i += 2) {
            float poly_x = polygon[i];
            float poly_y = polygon[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degrees);
            vertex.add(x, y);

            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = this.vertexIndex;
        for (int i = 0; i < triangles.length - 2; i += 3) {
            indices.put(startVertex + triangles[i + 0]);
            indices.put(startVertex + triangles[i + 1]);
            indices.put(startVertex + triangles[i + 1]);
            indices.put(startVertex + triangles[i + 2]);
            indices.put(startVertex + triangles[i + 2]);
            indices.put(startVertex + triangles[i + 0]);
        }
        vertexIndex += polygon.length / 2;

        vectors2Pool.free(vertex);
    }

    public void drawPolygonFilled(float[] polygon, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (!ensureCapacity(count, count * 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        ArrayFloat vertices = arrayFloatPool.allocate();
        ArrayInt triangles = arrayIntPool.allocate();
        try {
            MathUtils.polygonTriangulate(polygon, vertices, triangles);
        } catch (Exception e) { // Probably the polygon has collapsed into a single point.
            return;
        }

        Vector2 vertex = vectors2Pool.allocate();
        for (int i = 0; i < vertices.size; i += 2) {
            float poly_x = vertices.get(i);
            float poly_y = vertices.get(i + 1);

            vertex.set(poly_x, poly_y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degrees);
            vertex.add(x, y);

            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }
        vectors2Pool.free(vertex);

        int startVertex = this.vertexIndex;
        for (int i = 0; i < triangles.size; i ++) {
            indices.put(startVertex + triangles.get(i));
        }

        vertexIndex += count;
        arrayFloatPool.free(vertices);
        arrayIntPool.free(triangles);
    }

    public void drawPolygonFilled(float[] polygon) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (!ensureCapacity(count, count * 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        ArrayFloat vertices = arrayFloatPool.allocate();
        ArrayInt triangles = arrayIntPool.allocate();
        try {
            MathUtils.polygonTriangulate(polygon, vertices, triangles);
        } catch (Exception e) { // Probably the polygon has collapsed into a single point.
            return;
        }

        for (int i = 0; i < vertices.size; i += 2) {
            float poly_x = vertices.get(i);
            float poly_y = vertices.get(i + 1);
            positions.put(poly_x).put(poly_y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = this.vertexIndex;
        for (int i = 0; i < triangles.size; i ++) {
            indices.put(startVertex + triangles.get(i));
        }

        vertexIndex += count;
        arrayFloatPool.free(vertices);
        arrayIntPool.free(triangles);
    }

    public void drawPolygonFilled(float[] polygon, int[] triangles, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (!ensureCapacity(count, triangles.length)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 vertex = vectors2Pool.allocate();
        for (int i = 0; i < polygon.length; i += 2) {
            float poly_x = polygon[i];
            float poly_y = polygon[i + 1];

            vertex.set(poly_x, poly_y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degrees);
            vertex.add(x, y);

            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = this.vertexIndex;
        for (int triangle : triangles) {
            indices.put(startVertex + triangle);
        }
        vertexIndex += count;

        vectors2Pool.free(vertex);
    }

    /* Rendering 2D primitives - lines */

    public final void drawLineThin(float x1, float y1, float x2, float y2) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(2, 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

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

    public final void drawLineThin(float p1X, float p1Y, float p2X, float p2Y, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(2, 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        Vector2 vertex1 = vectors2Pool.allocate();
        vertex1.set(p1X, p1Y);
        vertex1.scl(scaleX, scaleY);
        vertex1.rotateDeg(degrees);
        vertex1.add(x, y);

        Vector2 vertex2 = vectors2Pool.allocate();
        vertex2.set(p2X, p2Y);
        vertex2.scl(scaleX, scaleY);
        vertex2.rotateDeg(degrees);
        vertex2.add(x, y);

        positions.put(vertex1.x).put(vertex1.y);
        positions.put(vertex2.x).put(vertex2.y);

        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        vertexIndex += 2;

        vectors2Pool.free(vertex1);
        vectors2Pool.free(vertex2);
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2, float thickness) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 dir = vectors2Pool.allocate();
        dir.x = x2 - x1;
        dir.y = y2 - y1;
        dir.nor();
        dir.scl(thickness * 0.5f);
        dir.rotate90(1);

        // put vertices for line segment
        positions.put(x1 + dir.x).put(y1 + dir.y);
        positions.put(x1 - dir.x).put(y1 - dir.y);
        positions.put(x2 - dir.x).put(y2 - dir.y);
        positions.put(x2 + dir.x).put(y2 + dir.y);

        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex + 0);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        vertexIndex += 4;

        vectors2Pool.free(dir);
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2, float thickness, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(4, 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2 dir = vectors2Pool.allocate();
        dir.x = x2 - x1;
        dir.y = y2 - y1;
        dir.nor();
        dir.scl(thickness * 0.5f);
        dir.rotate90(1);

        // put vertices for line segment
        Vector2 vertex1 = vectors2Pool.allocate();
        vertex1.set(x1 + dir.x, y1 + dir.y);
        vertex1.scl(scaleX, scaleY);
        vertex1.rotateDeg(degrees);
        vertex1.add(x, y);
        positions.put(vertex1.x).put(vertex1.y);

        Vector2 vertex2 = vectors2Pool.allocate();
        vertex2.set(x1 - dir.x, y1 - dir.y);
        vertex2.scl(scaleX, scaleY);
        vertex2.rotateDeg(degrees);
        vertex2.add(x, y);
        positions.put(vertex2.x).put(vertex2.y);

        Vector2 vertex3 = vectors2Pool.allocate();
        vertex3.set(x2 - dir.x, y2 - dir.y);
        vertex3.scl(scaleX, scaleY);
        vertex3.rotateDeg(degrees);
        vertex3.add(x, y);
        positions.put(vertex3.x).put(vertex3.y);

        Vector2 vertex4 = vectors2Pool.allocate();
        vertex4.set(x2 + dir.x, y2 + dir.y);
        vertex4.scl(scaleX, scaleY);
        vertex4.rotateDeg(degrees);
        vertex4.add(x, y);
        positions.put(vertex4.x).put(vertex4.y);

        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex + 0);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        vertexIndex += 4;

        vectors2Pool.free(vertex1);
        vectors2Pool.free(vertex2);
        vectors2Pool.free(vertex3);
        vectors2Pool.free(vertex4);
        vectors2Pool.free(dir);
    }

    /* Rendering 2D primitives - curves */

    public void drawCurveThin(final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if (!ensureCapacity(values.length, values.length * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        /* put vertices */
        for (Vector2 value : values) {
            positions.put(value.x).put(value.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }
        vertexIndex += values.length;
    }

    public void drawCurveThin(final Vector2[] values, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if (!ensureCapacity(values.length, values.length * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        /* put vertices */
        Vector2 vertex = vectors2Pool.allocate();
        for (Vector2 value : values) {
            vertex.set(value.x, value.y);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degrees);
            vertex.add(x, y);
            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }
        vectors2Pool.free(vertex);

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }
        vertexIndex += values.length;
    }

    public void drawCurveThin(float minX, float maxX, int refinement, Function<Float, Float> f) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if (!ensureCapacity(refinement, refinement * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        Vector2 vertex = vectors2Pool.allocate();
        for (int i = 0; i < refinement; i++) {
            vertex.x = minX + i * step;
            vertex.y = f.apply(vertex.x);
            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }
        vectors2Pool.free(vertex);

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }

        vertexIndex += refinement;
    }

    public void drawCurveThin(float minX, float maxX, int refinement, Function<Float, Float> f, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        refinement = Math.max(2, refinement);
        if (!ensureCapacity(refinement, refinement * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float step = (maxX - minX) / refinement;

        Vector2 vertex = vectors2Pool.allocate();
        for (int i = 0; i < refinement; i++) {
            vertex.x = minX + i * step;
            vertex.y = f.apply(vertex.x);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(degrees);
            vertex.add(x, y);
            positions.put(vertex.x).put(vertex.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }
        vectors2Pool.free(vertex);

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < refinement - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }

        vertexIndex += refinement;
    }

    // TODO bug here: when vertices are co-linear and folding (opposite direction).
    // TODO example input to reproduce error:
    // TODO [{-3,0}, {0,0}, {-1,0}]
    public void drawCurveFilled(float stroke, int smoothness, final Vector2... points) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Array<Vector2> vertices = drawCurveFilledGetVertices(stroke, smoothness, points);
        if (!ensureCapacity(vertices.size, vertices.size)) flush();

        /*
        In the case of curve rendering, we might have a case where the number of vertices exceeds the capacity of the entire batch.
        In that case, we write triangle by triangle.
        */
        if (vertices.size > VERTICES_CAPACITY) {
            for (int i = 0; i < vertices.size; i += 3) {
                Vector2 p0 = vertices.get(i + 0);
                Vector2 p1 = vertices.get(i + 1);
                Vector2 p2 = vertices.get(i + 2);
                drawTriangleFilled(p0, p1, p2);
            }
        } else {
            for (int i = 0; i < vertices.size; i++) {
                Vector2 vertex = vertices.get(i);
                positions.put(vertex.x).put(vertex.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
                indices.put(vertexIndex + i);
            }

            vertexIndex += vertices.size;
        }
    }

    public Array<Vector2> drawCurveFilledGetVertices(float stroke, int smoothness, Vector2... points) {
        if (points.length < 2) {
            return null;
        }

        float lineWidth = Math.abs(stroke) * 0.5f;
        Array<Vector2> vertices = new Array<>();
        Array<Vector2> middlePoints = new Array<>();  // middle points per each line segment.
        boolean closed = false;

        if (points.length == 2) {
            Vector2 midPoint = new Vector2();
            Vector2.midPoint(points[0], points[1], midPoint);
            createTriangles(points[0], midPoint, points[1], vertices, lineWidth, smoothness);
        } else {

            if (points[0].equals(points[points.length - 1])) {
                Vector2 p0 = new Vector2();
                Vector2.midPoint(points[0], points[1], p0);
                Vector2[] points2 = new Vector2[points.length + 1];
                points2[0] = p0;
                System.arraycopy(points, 1, points2, 1, points.length - 1);
                points2[points.length] = p0;
                points = points2;
                closed = true;
            }

            for (int i = 0; i < points.length - 1; i++) {
                if (i == 0) {
                    middlePoints.add(points[0]);
                } else if (i == points.length - 2) {
                    middlePoints.add(points[points.length - 1]);
                } else {
                    Vector2 mid = new Vector2();
                    Vector2.midPoint(points[i], points[i + 1], mid);
                    middlePoints.add(mid);
                }
            }

            for (int i = 1; i < middlePoints.size; i++) {
                createTriangles(middlePoints.get(i - 1), points[i], middlePoints.get(i), vertices, lineWidth, smoothness);
            }
        }

        if (!closed) {
            Vector2 p00 = vertices.get(0);
            Vector2 p01 = vertices.get(1);
            Vector2 p02 = points[1];
            Vector2 p10 = vertices.get(vertices.size - 1);
            Vector2 p11 = vertices.get(vertices.size - 3);
            Vector2 p12 = points[points.length - 2];
            createRoundCap(points[0], p00, p01, p02, smoothness, vertices);
            createRoundCap(points[points.length - 1], p10, p11, p12, smoothness, vertices);
        }

        return vertices;
    }

    private void createTriangles(Vector2 p0, Vector2 p1, Vector2 p2, Array<Vector2> verts, float width, int refinement) {
        var t0 = new Vector2(p1).sub(p0);
        var t2 = new Vector2(p2).sub(p1);

        t0.rotate90(1);
        t2.rotate90(1);

        float signedTriangleArea = MathUtils.areaTriangleSigned(p0, p1, p2);
        if (signedTriangleArea > 0) {
            t0.flip();
            t2.flip();
        }

        t0.nor();
        t2.nor();
        t0.scl(width);
        t2.scl(width);

        Vector2 pIntersection = new Vector2();
        MathUtils.segmentsIntersection(new Vector2(t0).add(p0), new Vector2(t0).add(p1), new Vector2(t2).add(p2), new Vector2(t2).add(p1), pIntersection);

        Vector2 anchor = new Vector2(pIntersection).sub(p1);
        float anchorLength = anchor.len();
        Vector2 p0p1 = new Vector2(p0).sub(p1);
        float p0p1Length = p0p1.len();
        Vector2 p1p2 = new Vector2(p1).sub(p2);
        float p1p2Length = p1p2.len();

        if (anchorLength > p0p1Length || anchorLength > p1p2Length) {

            verts.add(new Vector2(p0).add(t0));
            verts.add(new Vector2(p0).sub(t0));
            verts.add(new Vector2(p1).add(t0));

            verts.add(new Vector2(p0).sub(t0));
            verts.add(new Vector2(p1).add(t0));
            verts.add(new Vector2(p1).sub(t0));

            createRoundCap(p1, new Vector2(p1).add(t0), new Vector2(p1).add(t2), p2, refinement, verts);

            verts.add(new Vector2(p2).add(t2));
            verts.add(new Vector2(p1).sub(t2));
            verts.add(new Vector2(p1).add(t2));

            verts.add(new Vector2(p2).add(t2));
            verts.add(new Vector2(p1).sub(t2));
            verts.add(new Vector2(p2).sub(t2));

        } else {

            verts.add(new Vector2(p0).add(t0));
            verts.add(new Vector2(p0).sub(t0));
            verts.add(new Vector2(p1).sub(anchor));

            verts.add(new Vector2(p0).add(t0));
            verts.add(new Vector2(p1).sub(anchor));
            verts.add(new Vector2(p1).add(t0));

            Vector2 _p0 = new Vector2(p1).add(t0);
            Vector2 _p1 = new Vector2(p1).add(t2);
            Vector2 _p2 = new Vector2(p1).sub(anchor);

            Vector2 center = p1;

            verts.add(_p0);
            verts.add(center);
            verts.add(_p2);

            createRoundCap(center, _p0, _p1, _p2, refinement, verts);

            verts.add(center);
            verts.add(_p1);
            verts.add(_p2);

            verts.add(new Vector2(p2).add(t2));
            verts.add(new Vector2(p1).sub(anchor));
            verts.add(new Vector2(p1).add(t2));

            verts.add(new Vector2(p2).add(t2));
            verts.add(new Vector2(p1).sub(anchor));
            verts.add(new Vector2(p2).sub(t2));
        }
    }

    private void createRoundCap(Vector2 center, Vector2 _p0, Vector2 _p1, Vector2 nextPointInLine, int refinement, Array<Vector2> verts) {
        float radius = new Vector2(center).sub(_p0).len();

        float angle0 = MathUtils.atan2((_p1.y - center.y), (_p1.x - center.x));
        float angle1 = MathUtils.atan2((_p0.y - center.y), (_p0.x - center.x));

        float orgAngle0 = angle0;

        if (angle1 > angle0) {
            if (angle1 - angle0 >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR) {
                angle1 = angle1 - 2 * MathUtils.PI;
            }
        } else {
            if (angle0 - angle1 >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR) {
                angle0 = angle0 - 2 * MathUtils.PI;
            }
        }

        var angleDiff = angle1 - angle0;

        if (Math.abs(angleDiff) >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR && Math.abs(angleDiff) <= Math.PI + MathUtils.FLOAT_ROUNDING_ERROR) {
            var r1 = new Vector2(center).sub(nextPointInLine);
            if (r1.x == 0) {
                if (r1.y > 0) {
                    angleDiff = -angleDiff;
                }
            } else if (r1.x >= -MathUtils.FLOAT_ROUNDING_ERROR ) {
                angleDiff = -angleDiff;
            }
        }

        float da = angleDiff / refinement;
        for (int i = 0; i < refinement; i++) {
            verts.add(new Vector2(center.x, center.y));
            verts.add(new Vector2(
                    center.x + radius * MathUtils.cosRad(orgAngle0 + da * i),
                    center.y + radius * MathUtils.sinRad(orgAngle0 + da * i)
            ));
            verts.add(new Vector2(
                    center.x + radius * MathUtils.cosRad(orgAngle0 + da * (1 + i)),
                    center.y + radius * MathUtils.sinRad(orgAngle0 + da * (1 + i))
            ));
        }
    }

    /* Rendering 2D primitives - Bezier Curves */



    /* Rendering 2D primitives - triangles */

    public void drawTriangleFilled(@NotNull Texture texture,
                                   float x1, float y1, float u1, float v1,
                                   float x2, float y2, float u2, float v2,
                                   float x3, float y3, float u3, float v3) {
        drawTriangleFilled(texture,
                x1, y1, currentTint, u1, v1,
                x2, y2, currentTint, u2, v2,
                x3, y3, currentTint, u3, v3
        );
    }

    public void drawTriangleFilled(Vector2 p0,
                                   Vector2 p1,
                                   Vector2 p2) {
        drawTriangleFilled(null,
                p0.x, p0.y, currentTint, 0.5f, 0.5f,
                p1.x, p1.y, currentTint, 0.5f, 0.5f,
                p2.x, p2.y, currentTint, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(float x1, float y1,
                                   float x2, float y2,
                                   float x3, float y3) {
        drawTriangleFilled(null,
                x1, y1, currentTint, 0.5f, 0.5f,
                x2, y2, currentTint, 0.5f, 0.5f,
                x3, y3, currentTint, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(float x1, float y1, float c1,
                                   float x2, float y2, float c2,
                                   float x3, float y3, float c3) {
        drawTriangleFilled(null,
                x1, y1, c1, 0.5f, 0.5f,
                x2, y2, c2, 0.5f, 0.5f,
                x3, y3, c3, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(@Nullable Texture texture,
                                   float x1, float y1, float c1, float u1, float v1,
                                   float x2, float y2, float c2, float u2, float v2,
                                   float x3, float y3, float c3, float u3, float v3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(3, 3)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        positions.put(x1).put(y1);
        positions.put(x2).put(y2);
        positions.put(x3).put(y3);

        colors.put(c1);
        colors.put(c2);
        colors.put(c3);

        textCoords.put(u1).put(v1);
        textCoords.put(u2).put(v2);
        textCoords.put(u3).put(v3);

        indices.put(vertexIndex);
        indices.put(vertexIndex+1);
        indices.put(vertexIndex+2);
        vertexIndex += 3;
    }

    // TODO, maybe.
    public void drawTextBlock() {

    }

    /* Rendering 2D primitives - Strings */
    // allows text markup modifiers: <b> <i> <h> <ul> <del> <sup> <sub> <color=#fff>
    public void drawTextLine(final String text, int size, @Nullable Font font, boolean antialiasing, float x, float y) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (!ensureCapacity(text.length() * 4, text.length() * 4)) flush();
        if (font == null) font = defaultFont;

        setMode(GL11.GL_TRIANGLES);

        /* calculate the line total width */
        float total_width = 0;
        char prevChar = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            final Font.Glyph glyph = font.getGlyph(c, size, antialiasing);
            if (glyph == null) continue;
            if (prevChar != 0) total_width += glyph.kernings.getOrDefault(prevChar,0);
            total_width += glyph.advanceX;
            prevChar = c;
        }

        /* calculate the line total height */
        float maxAscent = 0;
        float maxDescent = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            final Font.Glyph glyph = font.getGlyph(c, size, antialiasing);
            if (glyph == null) continue;

            // Update ascent (distance from baseline to top of glyph)
            float ascent = glyph.bearingY;
            if (ascent > maxAscent) maxAscent = ascent;

            // Update descent (distance below baseline)
            float descent = (glyph.height - glyph.bearingY);
            if (descent > maxDescent) maxDescent = descent;
        }
        float total_height = (maxAscent + maxDescent);

        /* render a quad for every character */
        float penX = x - total_width * 0.5f;
        float penY = y - total_height * 0.5f;
        prevChar = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            final Font.Glyph glyph = font.getGlyph(c, size, antialiasing);
            if (glyph == null) continue;

            setTexture(glyph.texture);

            if (prevChar != 0) penX += glyph.kernings.getOrDefault(prevChar,0);

            /* calculate the quad's x, y, width, height */
            float char_x = penX + glyph.bearingX;
            float char_y = penY - (glyph.height - glyph.bearingY);
            float w = glyph.width;
            float h = glyph.height;

            /* calculate the quad's uv coordinates */
            float u0 = glyph.atlasX * glyph.texture.invWidth;
            float v0 = (glyph.atlasY) * glyph.texture.invHeight;
            float u1 = (glyph.atlasX + glyph.width) * glyph.texture.invWidth;
            float v1 = (glyph.atlasY + glyph.height) * glyph.texture.invHeight;

            /* put vertices */
            positions.put(char_x).put(char_y + h);
            colors.put(currentTint);
            textCoords.put(u0).put(v0);

            positions.put(char_x).put(char_y);
            colors.put(currentTint);
            textCoords.put(u0).put(v1);

            positions.put(char_x + w).put(char_y);
            colors.put(currentTint);
            textCoords.put(u1).put(v1);

            positions.put(char_x + w).put(char_y + h);
            colors.put(currentTint);
            textCoords.put(u1).put(v0);

            /* put indices */
            int startVertex = this.vertexIndex;
            indices.put(startVertex + 0);
            indices.put(startVertex + 1);
            indices.put(startVertex + 3);
            indices.put(startVertex + 3);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);
            vertexIndex += 4;

            penX += glyph.advanceX;
            penY += glyph.advanceY;
            prevChar = c;
        }
    }

    /* Rendering Ops: ensureCapacity(), flush(), end(), deleteAll(), createDefaults...() */

    private boolean ensureCapacity(int numVertices, int numIndices) {
        boolean hasSpaceVertices = VERTICES_CAPACITY - vertexIndex >= numVertices;
        boolean hasSpaceIndices  = indices.capacity() - indices.position() >= numIndices;
        return hasSpaceVertices && hasSpaceIndices;
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
        if (!drawing) throw new GraphicsException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
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
        defaultTexture.delete();
    }

    /* Create defaults: shader, texture (single white pixel), camera */

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
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

    /*
    creates a single-white-pixel texture.
     */
    private static Texture createDefaultTexture() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF)); // Red component
        buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));  // Green component
        buffer.put((byte) (0xFF));                      // Blue component
        buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF)); // Alpha component
        buffer.flip();

        return new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1);
    }

    private static Matrix4x4 createDefaultMatrix() {
        return new Matrix4x4().setToOrthographicProjection(-Graphics.getWindowWidth() / 2.0f, Graphics.getWindowWidth() / 2.0f, -Graphics.getWindowHeight() / 2.0f, Graphics.getWindowHeight() / 2.0f, 0, 100);
    }

    private static Font createDefaultFont() {
        try (InputStream inputStream = Renderer2D.class.getClassLoader().getResourceAsStream("LiberationSans-Regular.ttf")) {
            if (inputStream == null) throw new GraphicsException("Resource not found: " + "LiberationSans-Regular.ttf");
            /* Read the InputStream into a ByteBuffer */
            byte[] bytes = inputStream.readAllBytes();
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            buffer.flip(); // Prepare the buffer for reading
            return new Font(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}