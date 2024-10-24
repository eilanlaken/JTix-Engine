package com.heavybox.jtix.application_2;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input_2.Input;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// TODO: merge application with application window.
public class Application {

    private static boolean initialized = false;
    private static final Array<Runnable> applicationTasks = new Array<>();
    private static boolean running = false;

    private static long    windowHandle;
    private static boolean windowFocused = false;
    private static int     windowLastDragAndDropFileCount = 0;
    private static boolean windowRequestRendering = false;

    private static final Array<Runnable> windowTasks                  = new Array<>();
    private static final Array<String>   windowFilesDraggedAndDropped = new Array<>();

    private static int     windowPosX                   = -1;
    private static int     windowPosY                   = -1;
    private static int     windowWidth                  = 640*2;
    private static int     windowHeight                 = 480*2;
    private static int     windowMinWidth               = -1;
    private static int     windowMinHeight              = -1;
    private static int     windowMaxWidth               = -1;
    private static int     windowMaxHeight              = -1;
    private static boolean windowAutoMinimized          = true;
    private static boolean windowMinimized              = false;
    private static boolean windowMaximized              = false;
    private static String  windowIconPath               = null;
    private static boolean windowVisible                = true;
    private static boolean windowFullScreen             = false;
    private static String  windowTitle                  = "HeavyBox Game";
    private static boolean windowVSyncEnabled           = false;

    private static GLFWErrorCallback errorCallback;

    private static Scene currentScene = null;

    /* GLFW Window callbacks */
    private static final GLFWFramebufferSizeCallback windowResizeCallback = new GLFWFramebufferSizeCallback() {
        private volatile boolean requested;

        @Override
        public void invoke(long windowHandle, final int width, final int height) {
            // OLD RESIZE CODE
//            if (Configuration.GLFW_CHECK_THREAD0.get(true)) {
//                renderWindow(width, height);
//            } else {
//                if (requested) return;
//                requested = true;
//                addTask(() -> {
//                    requested = false;
//                    renderWindow(width, height);
//                });
//            }
            renderWindow(width, height);
            //GL20.glViewport(0, 0, width, height); // TODO: see
            windowWidth = width;
            windowHeight = height;
            Graphics.justResized = true;
        }
    };

    private static final GLFWWindowFocusCallback windowFocusChangeCallback = new GLFWWindowFocusCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean focused) {
            windowTasks.add(() -> windowFocused = focused);
        }
    };

    private static final GLFWWindowIconifyCallback windowMinimizedCallback = new GLFWWindowIconifyCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean minimized) {
            windowTasks.add(() -> windowMinimized = minimized);
        }
    };

    private static final GLFWWindowMaximizeCallback windowMaximizedCallback = new GLFWWindowMaximizeCallback() {
        @Override
        public synchronized void invoke(long windowHandle, final boolean maximized) {
            windowTasks.add(() -> windowMaximized = maximized);
        }
    };

    private static final GLFWWindowCloseCallback windowCloseCallback = new GLFWWindowCloseCallback() {
        @Override
        public synchronized void invoke(final long handle) {
            windowTasks.add(() -> GLFW.glfwSetWindowShouldClose(handle, false));
        }
    };

    private static final GLFWDropCallback windowFilesDroppedCallback = new GLFWDropCallback() {
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

    public static void init() {
        final ApplicationConfiguration config = new ApplicationConfiguration(); // defaults.
        init(config);
    }

    public static void init(final ApplicationConfiguration config) {
        if (initialized) throw new ApplicationException("Application window already created and initialized. Cannot call init() twice.");
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new ApplicationException("Unable to initialize GLFW.");
        //window = new ApplicationWindow();
        // initialize window

        GL.createCapabilities();
        Async.init();
        Graphics.init();
        Input.init();
        initialized = true;
    }

    public static void launch(@NotNull Scene scene) {
        if (running) throw new ApplicationException("Application already running. Function run() already called - Cannot call run() twice.");

        /* start the application with active scene */
        currentScene = scene;
        currentScene.setup();
        currentScene.start();

        /* main thread game loop */
        running = true;
        while (running && !GLFW.glfwWindowShouldClose(windowHandle)) {
            GLFW.glfwMakeContextCurrent(windowHandle);
            boolean windowRendered = windowRefresh();
            int targetFrameRate = Graphics.getTargetFps();

            Assets.update();
            Input.update();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : applicationTasks) {
                task.run();
            }
            synchronized (applicationTasks) {
                requestRendering = applicationTasks.size > 0;
                applicationTasks.clear();
            }

            if (requestRendering && !Graphics.isContinuousRendering()) {
                synchronized (applicationTasks) {
                    windowRequestRendering = true;
                }
            }
            if (!windowRendered) { // Sleep a few milliseconds in case no rendering was requested with continuous rendering disabled.
                try {
                    Thread.sleep(1000 / Graphics.getIdleFps());
                } catch (InterruptedException ignored) {
                    // ignore
                }
            } else if (targetFrameRate > 0) {
                Async.sync(targetFrameRate); // sleep as needed to meet the target frame-rate
            }
        }

        /* clean memory resources */ // TODO: clear Assets.
        currentScene.finish();
        Assets.clear(); // TODO: implement
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
        GLFW.glfwTerminate();
        errorCallback.free();
    }

    public void playScene(@NotNull Scene scene) {
        if (!running) throw new ApplicationException("Application not running. Function run() must be called with the starting scene, after init.");
        if (currentScene != null) {
            currentScene.finish();
        }
        currentScene = scene;
        currentScene.setup();
        currentScene.start();
    }

    @Deprecated public static synchronized void addTask(Runnable task) {
        applicationTasks.add(task);
    }

    private static void renderWindow(final int width, final int height) {
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
        Graphics.update();
        currentScene.update();
        GLFW.glfwSwapBuffers(windowHandle);
    }

    public static boolean windowRefresh() {
        for (Runnable task : windowTasks) {
            task.run();
        }
        boolean shouldRefresh = windowTasks.size > 0 || Graphics.isContinuousRendering();
        synchronized (windowTasks) {
            windowTasks.clear();
            shouldRefresh |= windowRequestRendering && !windowMinimized;
            windowRequestRendering = false;
        }

        if (shouldRefresh) {
            Graphics.update();
            currentScene.update();
            GLFW.glfwSwapBuffers(windowHandle);
        }

        return shouldRefresh;
    }

    /* Setters & Actions */

    public static void exit() {
        running = false;
    }

    public static void windowClose() {
        window.close();
    }

    public static void windowMinimize() {
        window.minimize();
    }

    public static void windowMaximize() {
        window.maximize();
    }

    public static void windowFocus() {
        window.focus();
    }

    public static void windowRestore() {
        window.restore();
    }

    public static void windowFlash() {
        window.flash();
    }

    protected void windowSetIcon(final String path) {
        windowIconPath = path;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);
        if (imageData == null) throw new com.heavybox.jtix.application.ApplicationException("Failed to load icon image: " + STBImage.stbi_failure_reason());
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
        iconBuffer.position(0).width(width.get(0)).height(height.get(0)).pixels(imageData);
        GLFW.glfwSetWindowIcon(windowHandle, iconBuffer);
        STBImage.stbi_image_free(imageData);
    }

    public static void windowSetTitle(final String title) {
        windowTitle = title;
        GLFW.glfwSetWindowTitle(windowHandle, title);
    }

    /* Getters */

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

}
