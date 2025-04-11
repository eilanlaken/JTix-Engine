package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.z_deprecated.z_graphics_old.VertexAttribute_old;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    private static final MemoryPool<RenderCommand> renderUnitPool = new MemoryPool<>(RenderCommand.class, 5);
    private static final Array<RenderCommand>      renderUnits    = new Array<>(false, 20);

    // defaults
    private static final Texture defaultTexture = createDefaultTexture();
    private static final Shader  defaultShader  = createDefaultShaderProgram();
    private static final Color   defaultColor   = Color.WHITE.clone();


    private static boolean drawing = false;

    private static Camera currentCamera = null;
    private static int    currentMode   = GL11.GL_TRIANGLES;
    private static Shader currentShader = defaultShader;

    // TODO: check if Renderer2D is currently rendering.
    public static void begin(Camera camera) {
        if (drawing) throw new GraphicsException("Cannot call begin() while drawing. Must call end()");
        if (camera == null) throw new GraphicsException("camera cannot be null when rendering using " + Renderer3D.class.getSimpleName() + ".begin(Camera camera).");

        GL11.glColorMask(true, true, true, true); // enable color buffer writes
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: enable!
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        currentCamera = camera;
        drawing = true;
        currentShader = defaultShader;

        // TODO
        ShaderBinder.bind(currentShader);
    }

    // TODO
    private void setShader() {

    }

    public static void drawModel_tmp(Model model, Matrix4x4 transform) {
        currentShader.bindUniform("u_transform", transform);
        currentShader.bindUniform("u_camera_combined", currentCamera.combined);
        GL30.glBindVertexArray(model.meshes[0].vaoId);

        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.meshes[0].vertexCount);
        GL20.glDisableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
    }

    public static void drawModel_tmp_2(ModelMesh mesh, Matrix4x4 transform) {
        defaultShader.bindUniform("u_transform", transform);
        defaultShader.bindUniform("u_camera_combined", currentCamera.combined);

        GL30.glBindVertexArray(mesh.vaoId);
        {
//            for (VertexAttribute attribute : VertexAttribute.values()) {
//                if (mesh.hasVertexAttribute(attribute)) {
//                    System.out.println(attribute);
//                    GL20.glEnableVertexAttribArray(attribute.glslLocation);
//                }
//            }
//            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
//            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
//            for (VertexAttribute attribute : VertexAttribute.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.glslLocation);

            GL20.glEnableVertexAttribArray(0);
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);
    }

    public static void drawModel_tmp_3(ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        currentShader.bindUniform("u_transform", transform);
        currentShader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.

        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");

        if (texture_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", texture_diffuse);
            currentShader.bindUniform("u_color_diffuse", Color.WHITE);
        } else if (color_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", defaultTexture);
            currentShader.bindUniform("u_color_diffuse", color_diffuse);
        } else { // TODO: handle error: missing both diffuse texture and color.

        }

        System.out.println(Integer.toBinaryString(currentShader.vertexAttributesBitmask));

        GL30.glBindVertexArray(mesh.vaoId);
        {
//            for (VertexAttribute attribute : VertexAttribute.values()) {
//                if (mesh.hasVertexAttribute(attribute)) {
//                    System.out.println(attribute);
//                    GL20.glEnableVertexAttribArray(attribute.glslLocation);
//                }
//            }
//            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
//            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
//            for (VertexAttribute attribute : VertexAttribute.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.glslLocation);

            // turn vbos on / off based on the shader and mesh

            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
                System.out.println(attribute.glslLocation);
            }

            GL20.glEnableVertexAttribArray(0); // positions
            GL20.glEnableVertexAttribArray(2); // uvs
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(2);

            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    public static void drawModel_tmp_4(ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        currentShader.bindUniform("u_transform", transform);
        currentShader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.

        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");

        if (texture_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", texture_diffuse);
            currentShader.bindUniform("u_color_diffuse", Color.WHITE);
        } else if (color_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", defaultTexture);
            currentShader.bindUniform("u_color_diffuse", color_diffuse);
        } else { // TODO: handle error: missing both diffuse texture and color.

        }

        GL30.glBindVertexArray(mesh.vaoId);
        {

            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
                System.out.println(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
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
        drawing = false;
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
            System.err.println("Could not create shader program from resources. Creating manually. Exception: " + e.getMessage());

            String vertexShader = """
                    #version 450
                      
                      // attributes
                      layout(location = 0) in vec3 a_position;
                      layout(location = 2) in vec2 a_textCoords0;
                      
                      // uniforms
                      uniform mat4 u_transform;
                      uniform mat4 u_camera_combined;
                      
                      out vec2 uv;
                      
                      void main() {
                          uv = a_textCoords0;
                          gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
                      }
                    """;

            String fragmentShader = """
                    #version 450
                     
                     // inputs
                     in vec2 uv;
                     
                     // uniforms
                     uniform sampler2D u_texture_diffuse;
                     
                     // outputs
                     layout (location = 0) out vec4 out_color;
                     
                     void main() {
                         out_color = texture(u_texture_diffuse, uv);
                     }""";

            return new Shader(vertexShader, fragmentShader);
        }
    }

    /*
    TODO: this is common to both Renderer2D and Renderer3D and should be refactored.
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

    private static final class RenderCommand implements MemoryPool.Reset {


        public ModelMesh mesh;
        public boolean isPrimitive; // represents a "primitive" shape: a quad, cube, sphere, function, curve. The vertices are calculated on the fly and stored in primitiveVertices.
        public FloatBuffer primitiveVertices; // interleaved


        public Shader materialShader = null;
        public HashMap<String, Object> materialAttributes = new HashMap<>();
        public Matrix4x4      transform = null;

        public RenderCommand() {

        }

        @Override
        public void reset() {
            this.mesh = null;
            primitiveVertices = null;
            materialShader = null;
            this.transform = null;
            materialAttributes.clear();
        }

    }

}

/*

public void draw(final ModelPart modelPart, final ComponentTransform_1 transform) {
        // TODO: maybe updating the bounding sphere should be somewhere else.
        float centerX = modelPart.mesh.boundingSphereCenter.x;
        float centerY = modelPart.mesh.boundingSphereCenter.y;
        float centerZ = modelPart.mesh.boundingSphereCenter.z;
        Vector3 boundingSphereCenter = new Vector3(centerX + transform.x, centerY + transform.y, centerZ + transform.z);
        float boundingSphereRadius = MathUtils.max(transform.scaleX, transform.scaleY, transform.scaleZ) * modelPart.mesh.boundingSphereRadius;
        if (componentGraphicsCamera.lens.frustumIntersectsSphere(boundingSphereCenter, boundingSphereRadius)) {
            System.out.println("intersects");
        } else {
            System.out.println("CULLING");
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // todo: see when it makes sense to compute the matrix transform
        currentShader.bindUniform("u_body_transform", transform.world());
        ModelPartMaterial material = modelPart.material;
        //currentShader.bindUniforms(material.materialParams);
        currentShader.bindUniform("colorDiffuse", material.uniformParams.get("colorDiffuse"));
        ModelPartMesh mesh = modelPart.mesh;
        System.out.println("ddddd " + mesh.vaoId);
        GL30.glBindVertexArray(mesh.vaoId);
        {
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) {
                System.out.println("attrib: " + attribute.slot);
                if (mesh.hasVertexAttribute(attribute)) GL20.glEnableVertexAttribArray(attribute.slot);
            }
            if (mesh.indexed) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.slot);
        }
        GL30.glBindVertexArray(0);
    }


 */