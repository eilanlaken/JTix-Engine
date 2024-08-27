package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.ShaderProgram;
import com.heavybox.jtix.graphics.TexturePack;

import java.util.HashMap;

public class ComponentGraphics2DSprite extends Component {

    public static final Component.Category category = Category.GRAPHICS;

    public final TexturePack.Region region;
    public Color tint;
    public ShaderProgram customShader;
    public HashMap<String, Object> customAttributes;

    protected ComponentGraphics2DSprite(TexturePack.Region region, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        super(category);
        this.region = region;
        this.tint = tint;
        this.customShader = customShader;
        this.customAttributes = customAttributes;
    }



}
