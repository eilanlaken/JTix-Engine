package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.ShaderProgram;

import java.util.Map;

public final class ComponentRender2DSprite extends ComponentRender2D {

    public boolean             active;
    public int                 zIndex;
    public ShaderProgram       shader;
    public Map<String, Object> shaderAttributes;
    public float               tint;
    public int                 pixelsPerUnit;


    @Override
    public void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // TODO: implement.
    }

}
