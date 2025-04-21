package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

public class ToolTreeStamp_old extends Tool {

    public TreeType currentType = TreeType.BRIGHT_GREEN;
    public int currentIndex = 0;
    public GameObject pending = new GameObject();

    @Override
    public void update() {
        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {

        }
    }

    @Override
    public void render() {

    }

    public enum TreeType {
        BRIGHT_GREEN,
        BROWN_RED,
        DARK_GREEN,
        GREEN_ORANGE,
        GREEN_RED,
        LIME_GREEN,
    }

}
