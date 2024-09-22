package com.heavybox.jtix.graphics;

import com.heavybox.jtix.application.ApplicationWindow;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

public final class GraphicsUtils {

    private static ApplicationWindow window      = null;
    private static boolean           initialized = false;

    private static boolean isContinuous = true;
    private static long    lastFrameTime = -1;
    private static float   deltaTime;
    private static boolean resetDeltaTime = false;
    private static long    frameId = 0;
    private static long    frameCounterStart = 0;
    private static int     frames = 0;
    private static int     fps;
    private static int     targetFps = 120;
    private static int     prevTargetFps = targetFps;
    private static int     idleFps = 10;
    private static int     maxTextureSize;
    private static int     maxAnisotropicFilterLevel = 0;
    private static float   contentScaleX;
    private static float   contentScaleY;

    private GraphicsUtils() {}

    public static void init(final ApplicationWindow window) {
        if (initialized) return;

        GraphicsUtils.window = window;
        maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        long monitor = GLFW.glfwGetPrimaryMonitor();
        /* set content scale */
        FloatBuffer px = BufferUtils.createFloatBuffer(1);
        FloatBuffer py = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(monitor, px, py);
        contentScaleX = px.get(0);
        contentScaleY = py.get(0);

        initialized = true;
    }

    public static void update() {
        long time = System.nanoTime();
        if (lastFrameTime == -1) lastFrameTime = time;
        if (resetDeltaTime) {
            resetDeltaTime = false;
            deltaTime = 0;
        } else
            deltaTime = (time - lastFrameTime) / 1000000000.0f;
        lastFrameTime = time;

        if (time - frameCounterStart >= 1000000000) {
            fps = frames;
            frames = 0;
            frameCounterStart = time;
        }
        frames++;
        frameId++;
    }

    public static float getContentScaleX() {
        return contentScaleX;
    }

    public static float getContentScaleY() {
        return contentScaleY;
    }

    public static int getFps() {
        return fps;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static int getIdleFps() {
        return idleFps;
    }

    public static void setIdleFps(int idleFps) {
        GraphicsUtils.idleFps = idleFps;
    }

    public static int getTargetFps() {
        return targetFps;
    }

    public static void setTargetFps(int targetFps) {
        prevTargetFps = GraphicsUtils.targetFps;
        GraphicsUtils.targetFps = targetFps;
    }

    public static int getMaxTextureSize() {
        return maxTextureSize;
    }

    public static int getFrameCount() {
        return frames;
    }

    public static void setContinuousRendering(boolean isContinuous) {
        GraphicsUtils.isContinuous = isContinuous;
    }

    public static boolean isContinuousRendering () {
        return isContinuous;
    }

    public static int getMonitorWidth() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).width();
    }

    public static int getMonitorHeight() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).height();
    }

    public static int getWindowHeight() {
        return window.attributes.height;
    }

    public static int getWindowWidth() {
        return window.attributes.width;
    }

    public static float getMonitorAspectRatio() {
        return getMonitorWidth() / (float) getMonitorHeight();
    }

    public static int getMaxVerticesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_VERTICES);
    }

    public static int getMaxIndicesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_INDICES);
    }

    public static float getWindowAspectRatio() {
        return getWindowWidth() / (float) getWindowHeight();
    }

    public static int getWindowPositionX() {
        return window.getPositionX();
    }

    public static int getWindowPositionY() {
        return window.getPositionY();
    }

    public static void enableVSync() {
        int refreshRate = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate();
        setTargetFps(refreshRate);
        window.setVSync(true);
    }

    public static int getMaxFragmentShaderTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static int getMaxBoundTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static void disableVSync() {
        window.setVSync(false);
        setTargetFps(prevTargetFps); // restore target refresh rate before vsync.
    }

    public static boolean isVSyncEnabled() {
        return window.attributes.vSyncEnabled;
    }

    public static boolean isAnisotropicFilteringSupported() {
        return GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic");
    }

    public static int getMaxAnisotropicFilterLevel() {
        if (maxAnisotropicFilterLevel > 0) return maxAnisotropicFilterLevel;

        if (GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic")) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            buffer.position(0);
            buffer.limit(buffer.capacity());
            GL20.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, buffer);
            maxAnisotropicFilterLevel = (int) buffer.get(0);
        } else {
            maxAnisotropicFilterLevel = 1;
        }

        return maxAnisotropicFilterLevel;
    }

}
