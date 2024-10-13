package com.heavybox.jtix.z_ecs_old;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.TexturePack;

import java.util.HashMap;

public class ComponentGraphics2DSprite extends Component {

    public static final Type TYPE = Type.GRAPHICS;

    public final TexturePack.Region region;
    public Color tint;
    public Shader customShader;
    public HashMap<String, Object> customAttributes;

    protected ComponentGraphics2DSprite(TexturePack.Region region, Color tint, Shader customShader, HashMap<String, Object> customAttributes) {
        super(TYPE);
        this.region = region;
        this.tint = tint;
        this.customShader = customShader;
        this.customAttributes = customAttributes;
    }



}
