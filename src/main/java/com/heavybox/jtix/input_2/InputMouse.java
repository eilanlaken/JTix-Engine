package com.heavybox.jtix.input_2;

import com.heavybox.jtix.application_2.Application;
import org.lwjgl.glfw.*;

public class InputMouse {

    /* mouse info */
    private float   sensitivity      = 1f; // goes from 0 to infinity. Default is 1.
    private int     prevCursorX      = 0;
    private int     prevCursorY      = 0;
    private int     cursorX          = 0;
    private int     cursorY          = 0;
    private int     cursorDeltaX     = 0;
    private int     cursorDeltaY     = 0;
    private boolean cursorHidden     = false;
    private boolean cursorInWindow   = true;
    private float   verticalScroll   = 0;
    private float   horizontalScroll = 0;

    /* mouse state */
    private final int[] mouseButtonsPrevStates    = new int[5];
    private final int[] mouseButtonsCurrentStates = new int[5];

    InputMouse() {
        GLFW.glfwSetMouseButtonCallback(Application.getWindowHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtonsPrevStates[button] = mouseButtonsCurrentStates[button];
                mouseButtonsCurrentStates[button] = action;
            }
        });

        GLFW.glfwSetCursorPosCallback(Application.getWindowHandle(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                prevCursorX = cursorX;
                prevCursorY = cursorY;
                cursorX = (int) xPos;
                cursorY = (int) yPos;
                cursorDeltaX = cursorX - prevCursorX;
                cursorDeltaY = cursorY - prevCursorY;
            }
        });

        GLFW.glfwSetCursorEnterCallback(Application.getWindowHandle(), new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                cursorInWindow = entered;
            }
        });

        GLFW.glfwSetScrollCallback(Application.getWindowHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                verticalScroll = (float) yOffset;
                horizontalScroll = (float) xOffset;
            }
        });
    }

    public float getVerticalScroll() {
        return verticalScroll;
    }

    public float getHorizontalScroll() {
        return horizontalScroll;
    }

    public void hideCursor() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        cursorHidden = true;
    }

    public void revealCursor() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        cursorHidden = false;
    }

    public boolean isCursorHidden() {
        return cursorHidden;
    }

    public boolean isInsideWindow() {
        return true;
    }

    public void setMouseSensitivity(float sensitivity) {
        sensitivity = sensitivity;
    }

    public float getMouseSensitivity() {
        return sensitivity;
    }

    public int getPrevCursorX() {
        return prevCursorX;
    }

    public int getPrevCursorY() {
        return prevCursorY;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    public int getCursorDeltaX() {
        return cursorDeltaX;
    }

    public int getCursorDeltaY() {
        return cursorDeltaY;
    }

    public boolean isCursorInWindow() {
        return cursorInWindow;
    }

    public boolean isButtonPressed(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_PRESS;
    }

    public boolean isButtonJustPressed(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsPrevStates[button.glfwCode] != GLFW.GLFW_PRESS;
    }

    public boolean isButtonReleased(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    public boolean isButtonClicked(final Button button) {
        return mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    void update() {
        /* reset internal state */
        verticalScroll = 0;
        horizontalScroll = 0;
        cursorDeltaX = 0;
        cursorDeltaY = 0;
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_1] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_1];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_2] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_2];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_3] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_3];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_4] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_4];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_5] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_5];
    }

    public enum Button {

        LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
        RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
        MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3),
        BACK(GLFW.GLFW_MOUSE_BUTTON_4),
        FORWARD(GLFW.GLFW_MOUSE_BUTTON_5)
        ;

        public final int glfwCode;

        Button(final int glfwCode) {
            this.glfwCode = glfwCode;
        }

    }

}
