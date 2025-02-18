package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

// TODO: implement.
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/FrameBuffer.java
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/GLFrameBuffer.java#L57
public class FrameBuffer implements MemoryResource {

    public final int width;
    public final int height;
    private final int fbo;
    private final int depthStencilRBO;

    private final Texture colorAttachment;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        // Create FrameBuffer
        fbo = GL30.glGenFramebuffers();
        FrameBufferBinder.bind(this);
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        colorAttachment = new Texture(width, height);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorAttachment.getHandle(), 0);

        depthStencilRBO = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilRBO);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilRBO);

        // Check if framebuffer is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GraphicsException("Could not create FrameBuffer. Error: " + "TODO.");
        }

        FrameBufferBinder.bind(null);
    }

    public int getFbo() {
        return fbo;
    }

    public Texture getColorAttachment() {
        return colorAttachment;
    }

    @Override
    public void delete() {
        GL30.glDeleteFramebuffers(fbo);
        colorAttachment.delete();
        GL30.glDeleteRenderbuffers(depthStencilRBO);
    }

}

/*
// TODO: when doing FrameBuffer.begin(), change the gl viewport to the size of the framebuffer.
// Then, change it back to the OG framebuffer dimensions.

try (MemoryStack stack = MemoryStack.stackPush()) {
    IntBuffer fbWidth = stack.mallocInt(1);
    IntBuffer fbHeight = stack.mallocInt(1);
    GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbHeight);
    GL20.glViewport(0, 0, fbWidth.get(0), fbHeight.get(0));
}

 */