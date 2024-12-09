package com.heavybox.jtix.graphics;

import com.heavybox.jtix.application.ApplicationWindow;
import com.heavybox.jtix.application_2.Application;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

public final class Graphics {

    @Deprecated private static ApplicationWindow window      = null;
    @Deprecated private static boolean           initialized = false;

    private static boolean isContinuous      = true;
    private static long    lastFrameTime     = -1;
    private static float   deltaTime; // TODO: maybe this belongs not in /graphics/ but in /application/
    private static boolean resetDeltaTime    = false;
    private static long    frameId           = 0;
    private static long    frameCounterStart = 0;
    private static int     frames            = 0;
    private static int     fps;
    private static int     targetFps         = 120;
    private static int     prevTargetFps     = targetFps;
    private static int     idleFps           = 10;
    private static int     maxTextureSize    = -1;
    private static int     maxAnisotropy     = 0;
    private static int     anisotropicFilteringSupported = -1;

    private Graphics() {}

    @Deprecated public static void init(final ApplicationWindow window) {
        if (initialized) return;

        Graphics.window = window;



        initialized = true;
    }

    public static void update() {
        long time = System.nanoTime();
        if (lastFrameTime == -1) lastFrameTime = time;
        if (resetDeltaTime) {
            resetDeltaTime = false;
            deltaTime = 0;
        } else {
            deltaTime = (time - lastFrameTime) / 1000000000.0f;
        }
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
        long monitor = GLFW.glfwGetPrimaryMonitor();
        FloatBuffer px = BufferUtils.createFloatBuffer(1);
        FloatBuffer py = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(monitor, px, py);
        return px.get(0);
    }

    public static float getContentScaleY() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        FloatBuffer px = BufferUtils.createFloatBuffer(1);
        FloatBuffer py = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(monitor, px, py);
        return py.get(0);
    }

    public static long getFrameId() { return frameId; }

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
        Graphics.idleFps = idleFps;
    }

    public static int getTargetFps() {
        return targetFps;
    }

    public static void setTargetFps(int targetFps) {
        prevTargetFps = Graphics.targetFps;
        Graphics.targetFps = targetFps;
    }

    public static int getMaxTextureSize() {
        if (maxTextureSize == -1) maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        return maxTextureSize;
    }

    public static int getFrameCount() {
        return frames;
    }

    public static void setContinuousRendering(boolean isContinuous) {
        Graphics.isContinuous = isContinuous;
    }

    public static boolean isContinuousRendering () {
        return isContinuous;
    }

    public static int getMonitorWidth() {
        return Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())).width();
    }

    public static int getMonitorHeight() {
        return Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())).height();
    }

    public static int getWindowHeight() {
        return Application.getWindowHeight();
    }

    public static int getWindowWidth() {
        return Application.getWindowWidth();
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

    // TODO: test
    public static void enableVSync() {
        int refreshRate = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate();
        setTargetFps(refreshRate);
        Application.enableVSync();
    }

    // TODO: test
    public static void disableVSync() {
        GLFW.glfwSwapInterval(1);
        setTargetFps(prevTargetFps); // restore target refresh rate before vsync.
        Application.disableVSync();
    }

    // TODO: test
    public static boolean isVSyncEnabled() {
        return Application.isVSyncEnabled();
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

    public static boolean isAnisotropicFilteringSupported() {
        if (anisotropicFilteringSupported == -1) {
            boolean supported = GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic");
            if (supported) anisotropicFilteringSupported = 1;
            else anisotropicFilteringSupported = 0;
        }

        return anisotropicFilteringSupported == 1;
    }

    public static int getMaxMSAA() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL30.GL_MAX_SAMPLES, intBuffer);
        return intBuffer.get(0);
    }

    public static int getMaxAnisotropy() {
        if (maxAnisotropy > 0) return maxAnisotropy;

        if (GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic")) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            buffer.position(0);
            buffer.limit(buffer.capacity());
            GL20.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, buffer);
            maxAnisotropy = (int) buffer.get(0);
        } else {
            maxAnisotropy = 1;
        }

        return maxAnisotropy;
    }

    public static float getMaxLineWidth() {
        float[] lineWidth = new float[2];
        GL11.glGetFloatv(GL11.GL_LINE_WIDTH_RANGE, lineWidth);
        return lineWidth[1];
    }

}
