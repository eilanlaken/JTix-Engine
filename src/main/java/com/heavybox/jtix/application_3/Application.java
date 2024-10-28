package com.heavybox.jtix.application_3;

import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.ecs.Scene;
import com.heavybox.jtix.graphics.Graphics;
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

    private static boolean initialized = false;
    private static Window window;
    private static final Array<Runnable> tasks = new Array<>();
    private static boolean running = false;
    private static GLFWErrorCallback errorCallback;

    // TODO: create a static block.
    public static void init() {
        init(new ApplicationSettings());
    }

    public static void init(final ApplicationSettings settings) {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
        window = new Window(settings);
        GL.createCapabilities();
        Async.init();
        Graphics.init(window);
        AssetUtils.init(window); // TODO: replace
        Mouse.init(window);
        Keyboard.init(window);
        initialized = true;
    }

    // TODO: implement
    public static void playScene(Scene scene) {

    }

    @Deprecated public static void launch(ApplicationScreen screen) {
        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
        window.setScreen(screen);
        running = true;
        loop();
        clean();
    }

    @Deprecated public static void switchScreen(ApplicationScreen screen) {
        window.setScreen(screen);
    }

    public static void loop() {
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = Graphics.getTargetFps();

            AssetStore.update(); // TODO: replace with new one.
            Mouse.update();
            Keyboard.update();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : tasks) {
                task.run();
            }
            synchronized (tasks) {
                requestRendering = tasks.size > 0;
                tasks.clear();
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

    public static void exit() {
        running = false;
    }

    public static synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

}
