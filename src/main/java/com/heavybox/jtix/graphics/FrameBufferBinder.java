package com.heavybox.jtix.graphics;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class FrameBufferBinder {

    private static FrameBuffer boundFrameBuffer;

    public static void bind() {
        bind(null);
    }

    public static void bind(@Nullable FrameBuffer frameBuffer) {
        if (Renderer2D_new.isDrawing()) throw new GraphicsException("Cannot switch frame buffers during a drawing sequence (between Renderer2D.begin() and Renderer2D.end(). Call Renderer2D.end() and only then bind a new frame buffer.");

        if (frameBuffer == null) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            boundFrameBuffer = null;
            GL20.glViewport(0, 0, Graphics.getWindowWidth(), Graphics.getWindowHeight());
            return;
        }

        if (boundFrameBuffer == frameBuffer) return; // prevent redundant frame buffer binds.

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer.getFbo());
        boundFrameBuffer = frameBuffer;
        GL20.glViewport(0, 0, frameBuffer.width, frameBuffer.height);
    }

}
