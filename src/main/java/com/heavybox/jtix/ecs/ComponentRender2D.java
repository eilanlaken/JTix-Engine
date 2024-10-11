package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.ShaderProgram;

import java.util.Map;

public abstract class ComponentRender2D implements ComponentRender {

    public boolean             active;
    public int                 zIndex;
    public ShaderProgram       shader;
    public Map<String, Object> shaderAttributes;
    public float               tint;
    public int                 pixelsPerUnit;

    public abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);

}
