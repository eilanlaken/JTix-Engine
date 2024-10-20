package com.heavybox.jtix.application_2;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.ecs.Scene;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.z_old_assets.AssetUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

// TODO: merge application with application window.
// TODO: as part of a refactor branch, create a singleton application.
// TODO: the Application static variable will be injected to the utils classes.
public class Application {

    private static final Application instance = new Application();

    private boolean initialized = false;
    public ApplicationWindow window; // TODO: "flatify" with application
    private final Array<Runnable> application_tasks = new Array<>();
    private boolean running = false;
    private GLFWErrorCallback errorCallback;

    static {
        instance.errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(instance.errorCallback);
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
        instance.window = new ApplicationWindow();
        GL.createCapabilities();
        Async.init();
        ApplicationUtils.init(instance);
        Graphics.init(instance);
        AssetUtils.init(instance); // TODO: replace
        Input.init(instance);
//        Mouse.init(window);
//        Keyboard.init(window);
        instance.initialized = true;
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

//    @Deprecated public static void switchScreen(ApplicationScreen screen) {
//        window.setScreen(screen);
//    }

    public void loop() {
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = Graphics.getTargetFps();

            AssetStore.update(); // TODO: replace with new one.
            Mouse.update();
            Keyboard.update();
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

    private void clean() {
        window.delete();
        GLFW.glfwTerminate();
        errorCallback.free();
    }

    public synchronized void addTask(Runnable task) {
        application_tasks.add(task);
    }

    public static void exit() {
        instance.running = false;
    }

    public static void windowClose() {
        instance.window.close();
    }

    public static void windowMinimize() {
        instance.window.minimize();
    }

    public static void windowMaximize() {
        instance.window.maximize();
    }

    public static void windowFocus() {
        instance.window.focus();
    }

    public static void windowRestore() {
        instance.window.restore();
    }

    public static void windowFlash() {
        instance.window.flash();
    }

    public static void windowSetIcon(final String path) {
        instance.window.setIcon(path);
    }

    public static void windowSetTitle(final String title) {
        instance.window.setTitle(title);
    }

}
