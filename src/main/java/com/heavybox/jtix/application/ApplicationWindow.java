package com.heavybox.jtix.application;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.ecs.Scene;
import com.heavybox.jtix.graphics.GraphicsUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class ApplicationWindow implements MemoryResource {

    // window attributes
    public final long                        handle;
    public final ApplicationWindowAttributes attributes;

    // state management
    private boolean focused = false;
    private int     lastDragAndDropFileCount = 0;
    private int     backBufferWidth;
    private int     backBufferHeight;
    private boolean requestRendering = false;

    private final Array<Runnable> tasks                  = new Array<>();
    private final Array<String>   filesDraggedAndDropped = new Array<>();

    // TODO: remove.
    private ApplicationScreen screen;
    // TODO: use this.
    private Scene currentScene;

    private final GLFWFramebufferSizeCallback resizeCallback = new GLFWFramebufferSizeCallback() {
        private volatile boolean requested;

        @Override
        public void invoke(long windowHandle, final int width, final int height) {
            if (Configuration.GLFW_CHECK_THREAD0.get(true)) {
                renderWindow(width, height);
            } else {
                if (requested) return;
                requested = true;
                Application.addTask(() -> {
                    requested = false;
                    renderWindow(width, height);
                });
            }
            ApplicationWindow.this.attributes.width = width;
            ApplicationWindow.this.attributes.height = height;
        }
    };

    private final GLFWWindowFocusCallback defaultFocusChangeCallback = new GLFWWindowFocusCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean focused) {
            tasks.add(() -> ApplicationWindow.this.focused = focused);
        }
    };

    private final GLFWWindowIconifyCallback defaultMinimizedCallback = new GLFWWindowIconifyCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean minimized) {
            tasks.add(() -> ApplicationWindow.this.attributes.minimized = minimized);
        }
    };

    private final GLFWWindowMaximizeCallback defaultMaximizedCallback = new GLFWWindowMaximizeCallback() {
        @Override
        public synchronized void invoke(long windowHandle, final boolean maximized) {
            tasks.add(() -> ApplicationWindow.this.attributes.maximized = maximized);
        }
    };

    private final GLFWWindowCloseCallback defaultCloseCallback = new GLFWWindowCloseCallback() {
        @Override
        public synchronized void invoke(final long handle) {
            tasks.add(() -> GLFW.glfwSetWindowShouldClose(handle, false));
        }
    };

    private final GLFWDropCallback filesDroppedCallback = new GLFWDropCallback() {
        @Override
        public synchronized void invoke(final long windowHandle, final int count, final long names) {
            tasks.add(() -> {
                lastDragAndDropFileCount = count;
                for (int i = 0; i < count; i++) {
                    filesDraggedAndDropped.add(GLFWDropCallback.getName(names, i));
                }
            });
        }
    };

    public ApplicationWindow(ApplicationWindowAttributes attributes) {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, attributes.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, attributes.maximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, attributes.autoMinimized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, attributes.transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW_FALSE);

        this.attributes = attributes;
        if (attributes.title == null) attributes.title = "";
        if (attributes.fullScreen) {
            // compute and auxiliary buffers
            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);
            assert videoMode != null;
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, videoMode.refreshRate());
            handle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title, videoMode.refreshRate(), MemoryUtil.NULL);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, attributes.decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            handle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title, MemoryUtil.NULL, MemoryUtil.NULL);
        }
        if (handle == MemoryUtil.NULL) throw new RuntimeException("Unable to create window.");
        setSizeLimits(attributes.minWidth, attributes.minHeight, attributes.maxWidth, attributes.maxHeight);
        // we need to set window position
        if (!attributes.fullScreen) {
            if (attributes.posX == -1 && attributes.posY == -1) setPosition(GraphicsUtils.getMonitorWidth() / 2 - attributes.width / 2,GraphicsUtils.getMonitorHeight() / 2 - attributes.height / 2);
            else setPosition(attributes.posX, attributes.posY);
            if (attributes.maximized) maximize();
        }

        if (attributes.iconPath != null) {
            setIcon(attributes.iconPath);
        }

        // register callbacks
        GLFW.glfwSetFramebufferSizeCallback(handle, resizeCallback);
        GLFW.glfwSetWindowFocusCallback(handle, defaultFocusChangeCallback);
        GLFW.glfwSetWindowIconifyCallback(handle, defaultMinimizedCallback);
        GLFW.glfwSetWindowMaximizeCallback(handle, defaultMaximizedCallback);
        GLFW.glfwSetWindowCloseCallback(handle, defaultCloseCallback);
        GLFW.glfwSetDropCallback(handle, filesDroppedCallback);
        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(attributes.vSyncEnabled ? 1 : 0);
        GLFW.glfwShowWindow(handle);
    }

    private void renderWindow(final int width, final int height) {
        updateFramebufferInfo();
        GLFW.glfwMakeContextCurrent(handle);
        GL20.glViewport(0, 0, backBufferWidth, backBufferHeight);
        screen.resize(width, height);
        GraphicsUtils.update();
        screen.refresh();
        GLFW.glfwSwapBuffers(handle);
    }

    private void updateFramebufferInfo() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Allocate two IntBuffers for framebuffer size
            IntBuffer fbWidth = stack.mallocInt(1);
            IntBuffer fbHeight = stack.mallocInt(1);
            // Get framebuffer size
            GLFW.glfwGetFramebufferSize(handle, fbWidth, fbHeight);
            this.backBufferWidth = fbWidth.get(0);
            this.backBufferHeight = fbHeight.get(0);
        }
    }

    public boolean refresh() {
        for (Runnable task : tasks) {
            task.run();
        }
        boolean shouldRefresh = tasks.size > 0 || GraphicsUtils.isContinuousRendering();
        synchronized (this) {
            tasks.clear();
            shouldRefresh |= requestRendering && !attributes.minimized;
            requestRendering = false;
        }

        if (shouldRefresh) {
            GraphicsUtils.update();
            screen.refresh();
            GLFW.glfwSwapBuffers(handle);
        }

        return shouldRefresh;
    }

    public void requestRendering() {
        synchronized (this) {
            this.requestRendering = true;
        }
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    protected void setDecorated(boolean decorated) {
        this.attributes.decorated = decorated;
        GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    protected void setTitle(final String title) {
        this.attributes.title = title;
        GLFW.glfwSetWindowTitle(handle, title);
    }

    protected void setIcon(final String path) {
        this.attributes.iconPath = path;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);
        if (imageData == null) throw new ApplicationException("Failed to load icon image: " + STBImage.stbi_failure_reason());
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
        iconBuffer.position(0).width(width.get(0)).height(height.get(0)).pixels(imageData);
        GLFW.glfwSetWindowIcon(handle, iconBuffer);
        STBImage.stbi_image_free(imageData);
    }

    protected void setPosition(int x, int y) {
        GLFW.glfwSetWindowPos(handle, x, y);
        attributes.posX = x;
        attributes.posY = y;
    }

    public int getPositionX() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer posX = stack.mallocInt(1); // For the X position, even if we don't use it
            IntBuffer posY = stack.mallocInt(1); // For the Y position
            GLFW.glfwGetWindowPos(handle, posX, posY);
            return posX.get(0);
        }
    }

    public int getPositionY() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer posX = stack.mallocInt(1); // For the X position, even if we don't use it
            IntBuffer posY = stack.mallocInt(1); // For the Y position
            GLFW.glfwGetWindowPos(handle, posX, posY);
            return posY.get(0);
        }
    }

    protected void setVisible(boolean visible) {
        if (visible) {
            GLFW.glfwShowWindow(handle);
        } else {
            GLFW.glfwHideWindow(handle);
        }
    }

    public void setVSync(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? 1 : 0);
        attributes.vSyncEnabled = enabled;
    }

    protected void close() {
        GLFW.glfwSetWindowShouldClose(handle, true);
    }

    protected void minimize() {
        GLFW.glfwIconifyWindow(handle);
        attributes.minimized = true;
    }

    protected boolean isMinimized() {
        return attributes.minimized;
    }

    protected void maximize() {
        GLFW.glfwMaximizeWindow(handle);
        attributes.maximized = true;
    }

    protected void flash() {
        GLFW.glfwRequestWindowAttention(handle);
    }

    protected void restore() {
        GLFW.glfwRestoreWindow(handle);
    }

    protected void focus() {
        GLFW.glfwFocusWindow(handle);
    }

    protected boolean isFocused() {
        return focused;
    }

    public Array<String> getFilesDraggedAndDropped() {
        return filesDraggedAndDropped;
    }

    public int getLastDragAndDropFileCount() {
        return lastDragAndDropFileCount;
    }

    @Deprecated public void setScreen(ApplicationScreen screen) {
        if (this.screen != null) {
            this.screen.hide();
            this.screen.window = null;
        }
        this.screen = screen;
        this.screen.show();
        this.screen.window = this;
    }

    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.glfwSetWindowSizeLimits(handle, minWidth > -1 ? minWidth : GLFW.GLFW_DONT_CARE,
                minHeight > -1 ? minHeight : GLFW.GLFW_DONT_CARE, maxWidth > -1 ? maxWidth : GLFW.GLFW_DONT_CARE,
                maxHeight > -1 ? maxHeight : GLFW.GLFW_DONT_CARE);
    }

    public long getHandle() {
        return handle;
    }


    @Override
    public void delete() {
        GLFW.glfwSetWindowFocusCallback(handle, null);
        GLFW.glfwSetWindowIconifyCallback(handle, null);
        GLFW.glfwSetWindowCloseCallback(handle, null);
        GLFW.glfwSetDropCallback(handle, null);
        GLFW.glfwDestroyWindow(handle);

        defaultFocusChangeCallback.free();
        defaultMinimizedCallback.free();
        defaultMaximizedCallback.free();
        defaultCloseCallback.free();
        filesDroppedCallback.free();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ApplicationWindow other = (ApplicationWindow)obj;
        return handle == other.handle;
    }

}