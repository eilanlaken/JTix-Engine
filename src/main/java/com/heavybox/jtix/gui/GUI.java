package com.heavybox.jtix.gui;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public final class GUI {

    private static final MemoryPool<Vector2> vector2Pool = new MemoryPool<>(Vector2.class, 1);

    /* API for creating buttons, paragraphs etc. */
    public static UIButton createButton(float width, float height, String text) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        UIButton btn = new UIButton(text);
        return btn;
    }



}
