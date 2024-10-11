package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.ShaderProgram;

import java.util.Map;

public abstract class ComponentRender2D implements ComponentRender {

    public boolean             active           = true;
    public int                 zIndex           = 0;
    public ShaderProgram       shader           = null;
    public Map<String, Object> shaderAttributes = null;
    public float               tint             = Color.WHITE.toFloatBits();
    public int                 pixelsPerUnit    = 100;

    public abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);

}
