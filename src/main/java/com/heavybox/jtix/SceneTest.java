package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.collections.Array;

public class SceneTest implements Scene {

    @Override
    public void setup() {
        System.out.println("setup");
    }

    @Override
    public void start() {
        System.out.println("start");

    }

    @Override
    public void update() {
        //System.out.println("looping");

    }

    @Override
    public void finish() {
        System.out.println("finish");

    }


    @Override
    public void windowResized(int width, int height) {
        System.out.println("resized: " + width + ", " + height);
    }

    @Override
    public void windowFocused(boolean focus) {
        System.out.println("focus: " + focus);
    }

    @Override
    public void windowMinimized(boolean minimized) {
        System.out.println(minimized);
    }

    @Override
    public void windowMaximized(boolean maximized) {
        Scene.super.windowMaximized(maximized);
    }

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }
}
