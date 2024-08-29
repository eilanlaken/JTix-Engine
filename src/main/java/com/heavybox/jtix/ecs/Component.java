package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.ShaderProgram;
import com.heavybox.jtix.graphics.TexturePack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Component {

    public final Type type;

    public Component(@NotNull final Type type) {
        this.type = type;
    }

    public enum Type {

        TRANSFORM,
        PHYSICS,
        GRAPHICS,
        AUDIO,
        LOGIC,
        CHEMISTRY,
        ;

        public final int bitMask;

        Type() {
            this.bitMask = 0b000001 << ordinal();
        }

    }

    /** Transforms **/
    public static ComponentTransform createTransform() {
        return new ComponentTransform(false, null,0,0,0,0,0,0,1,1,1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        return new ComponentTransform(isStatic,null,x, y, z, angleX, angleY, angleZ, scaleX, scaleY, scaleZ);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z) {
        return new ComponentTransform(isStatic,null, x, y, z, 0, 0, 0, 1, 1, 1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ) {
        return new ComponentTransform(isStatic,null, x, y, z, angleX, angleY, angleZ, 1, 1, 1);
    }

    /** Graphics - Sprites **/
    public static ComponentGraphics2DSprite createSprite(TexturePack.Region region, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        return new ComponentGraphics2DSprite(region, tint, customShader, customAttributes);
    }

    /** Graphics - Shapes **/



}
