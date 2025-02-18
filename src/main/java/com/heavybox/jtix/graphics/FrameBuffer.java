package com.heavybox.jtix.graphics;

// TODO: implement.
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/FrameBuffer.java
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/GLFrameBuffer.java#L57
public class FrameBuffer {

    public int width;
    public int height;
    public int handle;


    public void begin() {

    }

    public void end() {

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