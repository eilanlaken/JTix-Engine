package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.ShaderProgram;
import com.heavybox.jtix.graphics.TextureRegion;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Component {

    public final Category category;

    public Component(final Category category) {
        this.category = category;
    }

    public enum Category {

        TRANSFORM,
        PHYSICS,
        GRAPHICS,
        AUDIO,
        LOGIC,
        CHEMISTRY,
        ;

        public final int bitMask;

        Category() {
            this.bitMask = 0b000001 << ordinal();
        }

    }

    /** Transforms **/
    public static ComponentTransform createTransform() {
        return new ComponentTransform(false,0,0,0,0,0,0,1,1,1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        return new ComponentTransform(isStatic, x, y, z, angleX, angleY, angleZ, scaleX, scaleY, scaleZ);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z) {
        return new ComponentTransform(isStatic, x, y, z, 0, 0, 0, 1, 1, 1);
    }
    public static ComponentTransform createTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ) {
        return new ComponentTransform(isStatic, x, y, z, angleX, angleY, angleZ, 1, 1, 1);
    }

    /** Graphics - Sprites **/
    public static ComponentGraphics2DSprite createSprite(TextureRegion region, Color tint, ShaderProgram customShader, HashMap<String, Object> customAttributes) {
        return new ComponentGraphics2DSprite(region, tint, customShader, customAttributes);
    }

    /** Graphics - Sprite Animations **/
    public static ComponentGraphics2DSpriteAnimations createSpriteAnimations() {
        return null;
    }

    /** Graphics - Shapes **/



}
