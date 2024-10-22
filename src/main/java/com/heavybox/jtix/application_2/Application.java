package com.heavybox.jtix.application_2;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.ecs.Scene;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input_2.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

// TODO: merge application with application window.
public class Application {

    private static boolean initialized = false;
    public static ApplicationWindow window; // TODO: "flatify" with application
    private static final Array<Runnable> application_tasks = new Array<>();
    private static boolean running = false;

    public int     windowPosX                   = -1;
    public int     windowPosY                   = -1;
    public int     windowWidth                  = 640*2;
    public int     windowHeight                 = 480*2;
    public int     windowMinWidth               = -1;
    public int     windowMinHeight              = -1;
    public int     windowMaxWidth               = -1;
    public int     windowMaxHeight              = -1;
    public boolean autoMinimized          = true;
    public boolean minimized              = false;
    public boolean maximized              = false;
    public String  iconPath               = null;
    public boolean visible                = true;
    public boolean fullScreen             = false;
    public String  title                  = "JTix Game";
    public boolean vSyncEnabled           = false;

    private static GLFWErrorCallback errorCallback;

    private static void init() {
        if (initialized) return;
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
        window = new ApplicationWindow();
        GL.createCapabilities();
        Async.init();
        Graphics.init();
        Assets.init(); // TODO: replace
        Input.init();
        initialized = true;
    }

    // TODO: implement
    public static void playScene(Scene scene) {

    }

    @Deprecated public static void launch(ApplicationScreen screen) {
//        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
//        window.setScreen(screen);
//        running = true;
//        loop();
//        clean();
    }

    public static void loop() {
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

            if (requestRendering && !Graphics.isContinuousRendering()) window.requestRendering();
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

    private static void clean() {
        window.delete();
        GLFW.glfwTerminate();
        errorCallback.free();
    }

    public static synchronized void addTask(Runnable task) {
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
