package com.heavybox.jtix.z_ecs_old;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.TextureRegion;
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
    public static ComponentTransform_1 createTransform() {
        return new ComponentTransform_1(null,0,0,0,0,0,0,1,1,1);
    }
    public static ComponentTransform_1 createTransform(float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        return new ComponentTransform_1(null,x, y, z, angleX, angleY, angleZ, scaleX, scaleY, scaleZ);
    }
    public static ComponentTransform_1 createTransform(float x, float y, float z) {
        return new ComponentTransform_1(null, x, y, z, 0, 0, 0, 1, 1, 1);
    }
    public static ComponentTransform_1 createTransform(float x, float y, float z, float angleX, float angleY, float angleZ) {
        return new ComponentTransform_1(null, x, y, z, angleX, angleY, angleZ, 1, 1, 1);
    }

    /** Graphics - Sprites **/
    public static ComponentGraphics2DSprite createSprite(TextureRegion region, Color tint, Shader customShader, HashMap<String, Object> customAttributes) {
        return new ComponentGraphics2DSprite(region, tint, customShader, customAttributes);
    }

    /** Graphics - Shapes **/



}
