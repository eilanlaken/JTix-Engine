package com.heavybox.jtix.application_2;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationException;
import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
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

@Deprecated public class ApplicationWindow implements MemoryResource {

    // window attributes
    public final long windowHandle;
    public final ApplicationConfiguration attributes;

    // state management
    private boolean windowFocused = false;
    private int windowLastDragAndDropFileCount = 0;
    private boolean windowRequestRendering = false;

    private final Array<Runnable> windowTasks = new Array<>();
    private final Array<String> windowFilesDraggedAndDropped = new Array<>();

    private Scene scene;

    private final GLFWFramebufferSizeCallback windowResizeCallback = new GLFWFramebufferSizeCallback() {
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

    private final GLFWWindowFocusCallback windowFocusChangeCallback = new GLFWWindowFocusCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean focused) {
            windowTasks.add(() -> ApplicationWindow.this.windowFocused = focused);
        }
    };

    private final GLFWWindowIconifyCallback windowMinimizedCallback = new GLFWWindowIconifyCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean minimized) {
            windowTasks.add(() -> ApplicationWindow.this.attributes.minimized = minimized);
        }
    };

    private final GLFWWindowMaximizeCallback windowMaximizedCallback = new GLFWWindowMaximizeCallback() {
        @Override
        public synchronized void invoke(long windowHandle, final boolean maximized) {
            windowTasks.add(() -> ApplicationWindow.this.attributes.maximized = maximized);
        }
    };

    private final GLFWWindowCloseCallback windowCloseCallback = new GLFWWindowCloseCallback() {
        @Override
        public synchronized void invoke(final long handle) {
            windowTasks.add(() -> GLFW.glfwSetWindowShouldClose(handle, false));
        }
    };

    private final GLFWDropCallback windowFilesDroppedCallback = new GLFWDropCallback() {
        @Override
        public synchronized void invoke(final long windowHandle, final int count, final long names) {
            windowTasks.add(() -> {
                windowLastDragAndDropFileCount = count;
                for (int i = 0; i < count; i++) {
                    windowFilesDraggedAndDropped.add(GLFWDropCallback.getName(names, i));
                }
            });
        }
    };

    public ApplicationWindow() {
        this.attributes = new ApplicationConfiguration(); // remove.

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, attributes.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, attributes.maximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, attributes.autoMinimized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, attributes.transparentWindow ? GLFW.GLFW_TRUE : GLFW_FALSE);

        if (attributes.title == null) attributes.title = "";
        if (attributes.fullScreen) {
            // compute and auxiliary buffers
            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);
            assert videoMode != null;
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, videoMode.refreshRate());
            windowHandle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title, videoMode.refreshRate(), MemoryUtil.NULL);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, attributes.decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            windowHandle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title, MemoryUtil.NULL, MemoryUtil.NULL);
        }
        if (windowHandle == MemoryUtil.NULL) throw new RuntimeException("Unable to create window.");
        setSizeLimits(attributes.minWidth, attributes.minHeight, attributes.maxWidth, attributes.maxHeight);
        // we need to set window position
        if (!attributes.fullScreen) {
            if (attributes.posX == -1 && attributes.posY == -1) setPosition(Graphics.getMonitorWidth() / 2 - attributes.width / 2, Graphics.getMonitorHeight() / 2 - attributes.height / 2);
            else setPosition(attributes.posX, attributes.posY);
            if (attributes.maximized) maximize();
        }

        if (attributes.iconPath != null) {
            setIcon(attributes.iconPath);
        }

        // register callbacks
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, windowResizeCallback);
        GLFW.glfwSetWindowFocusCallback(windowHandle, windowFocusChangeCallback);
        GLFW.glfwSetWindowIconifyCallback(windowHandle, windowMinimizedCallback);
        GLFW.glfwSetWindowMaximizeCallback(windowHandle, windowMaximizedCallback);
        GLFW.glfwSetWindowCloseCallback(windowHandle, windowCloseCallback);
        GLFW.glfwSetDropCallback(windowHandle, windowFilesDroppedCallback);
        GLFW.glfwMakeContextCurrent(windowHandle);
        GLFW.glfwSwapInterval(attributes.vSyncEnabled ? 1 : 0);
        GLFW.glfwShowWindow(windowHandle);
    }

    private void renderWindow(final int width, final int height) {
        // update frame buffer info
        int backBufferWidth;
        int backBufferHeight;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer fbWidth = stack.mallocInt(1);
            IntBuffer fbHeight = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbHeight);
            backBufferWidth = fbWidth.get(0);
            backBufferHeight = fbHeight.get(0);
        }
        GLFW.glfwMakeContextCurrent(windowHandle);
        GL20.glViewport(0, 0, backBufferWidth, backBufferHeight);
        scene.windowResized(width, height);
        Graphics.update();
        scene.update();
        GLFW.glfwSwapBuffers(windowHandle);
    }

    public boolean windowRefresh() {
        for (Runnable task : windowTasks) {
            task.run();
        }
        boolean shouldRefresh = windowTasks.size > 0 || Graphics.isContinuousRendering();
        synchronized (this) {
            windowTasks.clear();
            shouldRefresh |= windowRequestRendering && !attributes.minimized;
            windowRequestRendering = false;
        }

        if (shouldRefresh) {
            Graphics.update();
            scene.update();
            GLFW.glfwSwapBuffers(windowHandle);
        }

        return shouldRefresh;
    }

    public void windowRequestRendering() {
        synchronized (this) {
            this.windowRequestRendering = true;
        }
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    protected void setDecorated(boolean decorated) {
        this.attributes.decorated = decorated;
        GLFW.glfwSetWindowAttrib(windowHandle, GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    protected void setTitle(final String title) {
        this.attributes.title = title;
        GLFW.glfwSetWindowTitle(windowHandle, title);
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
        GLFW.glfwSetWindowIcon(windowHandle, iconBuffer);
        STBImage.stbi_image_free(imageData);
    }

    protected void setPosition(int x, int y) {
        GLFW.glfwSetWindowPos(windowHandle, x, y);
        attributes.posX = x;
        attributes.posY = y;
    }

    public int getPositionX() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer posX = stack.mallocInt(1); // For the X position, even if we don't use it
            IntBuffer posY = stack.mallocInt(1); // For the Y position
            GLFW.glfwGetWindowPos(windowHandle, posX, posY);
            return posX.get(0);
        }
    }

    public int getPositionY() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer posX = stack.mallocInt(1); // For the X position, even if we don't use it
            IntBuffer posY = stack.mallocInt(1); // For the Y position
            GLFW.glfwGetWindowPos(windowHandle, posX, posY);
            return posY.get(0);
        }
    }

    protected void setVisible(boolean visible) {
        if (visible) {
            GLFW.glfwShowWindow(windowHandle);
        } else {
            GLFW.glfwHideWindow(windowHandle);
        }
    }

    public void setVSync(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? 1 : 0);
        attributes.vSyncEnabled = enabled;
    }

    protected void close() {
        GLFW.glfwSetWindowShouldClose(windowHandle, true);
    }

    protected void minimize() {
        GLFW.glfwIconifyWindow(windowHandle);
        attributes.minimized = true;
    }

    protected boolean isMinimized() {
        return attributes.minimized;
    }

    protected void maximize() {
        GLFW.glfwMaximizeWindow(windowHandle);
        attributes.maximized = true;
    }

    protected void flash() {
        GLFW.glfwRequestWindowAttention(windowHandle);
    }

    protected void restore() {
        GLFW.glfwRestoreWindow(windowHandle);
    }

    protected void focus() {
        GLFW.glfwFocusWindow(windowHandle);
    }

    protected boolean isWindowFocused() {
        return windowFocused;
    }

    public Array<String> getWindowFilesDraggedAndDropped() {
        return windowFilesDraggedAndDropped;
    }

    public int getWindowLastDragAndDropFileCount() {
        return windowLastDragAndDropFileCount;
    }

    @Deprecated public void setScreen(ApplicationScreen screen) {
//        if (this.screen != null) {
//            this.screen.hide();
//            this.screen.window = null;
//        }
//        this.screen = screen;
//        this.screen.show();
//        this.screen.window = this;
    }

    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.glfwSetWindowSizeLimits(windowHandle, minWidth > -1 ? minWidth : GLFW.GLFW_DONT_CARE,
                minHeight > -1 ? minHeight : GLFW.GLFW_DONT_CARE, maxWidth > -1 ? maxWidth : GLFW.GLFW_DONT_CARE,
                maxHeight > -1 ? maxHeight : GLFW.GLFW_DONT_CARE);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    @Override
    public void delete() {
        GLFW.glfwSetWindowFocusCallback(windowHandle, null);
        GLFW.glfwSetWindowIconifyCallback(windowHandle, null);
        GLFW.glfwSetWindowCloseCallback(windowHandle, null);
        GLFW.glfwSetDropCallback(windowHandle, null);
        GLFW.glfwDestroyWindow(windowHandle);

        windowFocusChangeCallback.free();
        windowMinimizedCallback.free();
        windowMaximizedCallback.free();
        windowCloseCallback.free();
        windowFilesDroppedCallback.free();
    }

}