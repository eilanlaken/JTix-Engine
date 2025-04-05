package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryPool;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// TODO:
// draw model surface material ("regular" drawing.)
// draw model wireframe
// draw model volume (smoke, clouds, water, jelly, ...)
// render objects with opacity.
public class Renderer3D {

    private static final MemoryPool<RenderUnit> renderUnitPool = new MemoryPool<>(RenderUnit.class, 5);
    private static final Array<RenderUnit>      renderUnits    = new Array<>(false, 20);

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

    private static final class RenderUnit implements MemoryPool.Reset {

        public Model.Mesh mesh;
        public Model.Material material;
        public Matrix4x4     transform;

        @Override
        public void reset() {
            this.mesh = null;
            this.material = null;
            this.transform = null;
        }

    }

}
