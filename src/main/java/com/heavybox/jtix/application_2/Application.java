package com.heavybox.jtix.application_2;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input_2.Input;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

// TODO: merge application with application window.
public class Application {

    private static boolean initialized = false;
    private static final Array<Runnable> application_tasks = new Array<>();
    private static boolean running = false;

    @Deprecated public static ApplicationWindow window; // TODO: "flatify" with application
    public  long                  windowHandle;
    private boolean windowFocused = false;
    private int     windowLastDragAndDropFileCount = 0;
    private boolean windowRequestRendering = false;

    private final Array<Runnable> windowTasks                  = new Array<>();
    private final Array<String>   windowFilesDraggedAndDropped = new Array<>();

    public int     windowPosX                   = -1;
    public int     windowPosY                   = -1;
    public int     windowWidth                  = 640*2;
    public int     windowHeight                 = 480*2;
    public int     windowMinWidth               = -1;
    public int     windowMinHeight              = -1;
    public int     windowMaxWidth               = -1;
    public int     windowMaxHeight              = -1;
    public boolean windowAutoMinimized          = true;
    public boolean windowMinimized              = false;
    public boolean windowMaximized              = false;
    public String  windowIconPath               = null;
    public boolean windowVisible                = true;
    public boolean windowFullScreen             = false;
    public String  windowTitle                  = "HeavyBox Game";
    public boolean windowVSyncEnabled           = false;

    private static GLFWErrorCallback errorCallback;

    private static Scene currentScene = null;

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
        window = new ApplicationWindow();
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
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = Graphics.getTargetFps();

            Assets.update();
            Input.update();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : application_tasks) {
                task.run();
            }
            synchronized (application_tasks) {
                requestRendering = application_tasks.size > 0;
                application_tasks.clear();
            }

            if (requestRendering && !Graphics.isContinuousRendering()) {
                window.requestRendering();
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
        window.delete();
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

    @Deprecated private static void frameUpdate() {
        GLFW.glfwMakeContextCurrent(window.getHandle());
        boolean windowRendered = window.refresh();
        int targetFrameRate = Graphics.getTargetFps();

        Assets.update();
        Input.update();
        GLFW.glfwPollEvents();

        boolean requestRendering;
        for (Runnable task : application_tasks) {
            task.run();
        }
        synchronized (application_tasks) {
            requestRendering = application_tasks.size > 0;
            application_tasks.clear();
        }

        if (requestRendering && !Graphics.isContinuousRendering()) {
            window.requestRendering();
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
    @Deprecated public static void loop() {
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = Graphics.getTargetFps();

            Assets.update();
            Input.update();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : application_tasks) {
                task.run();
            }
            synchronized (application_tasks) {
                requestRendering = application_tasks.size > 0;
                application_tasks.clear();
            }

            if (requestRendering && !Graphics.isContinuousRendering()) {
                window.requestRendering();
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
    }
    @Deprecated private static void clean() {
        window.delete();
        GLFW.glfwTerminate();
        errorCallback.free();
    }
    @Deprecated public static synchronized void addTask(Runnable task) {
        application_tasks.add(task);
    }

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

    public static void windowSetIcon(final String path) {
        window.setIcon(path);
    }

    public static void windowSetTitle(final String title) {
        window.setTitle(title);
    }

}
