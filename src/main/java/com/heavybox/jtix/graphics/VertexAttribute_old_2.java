package com.heavybox.jtix.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

public enum VertexAttribute_old_2 {

    POSITION_2D        (GL20.GL_FLOAT_VEC2,2,"a_position",       2, GL11.GL_FLOAT,             false),
    POSITION_3D        (GL20.GL_FLOAT_VEC3,3,"a_position",       3, GL11.GL_FLOAT,             false),
    COLOR              (GL20.GL_FLOAT_VEC4,4,"a_color",          1, GL11.GL_UNSIGNED_BYTE,     true),
    COLOR_HDR          (GL20.GL_FLOAT_VEC4,4,"a_colorHDR",       1, GL11.GL_FLOAT,             false),
    TEXT_COORDS0       (GL20.GL_FLOAT_VEC2,2,"a_textCoords0",    2, GL11.GL_FLOAT,             false),
    TEXT_COORDS1       (GL20.GL_FLOAT_VEC2,2,"a_textCoords1",    2, GL11.GL_FLOAT,             false),
    NORMAL             (GL20.GL_FLOAT_VEC3,3,"a_normal",         3, GL33.GL_INT_2_10_10_10_REV,true),
    TANGENT            (GL20.GL_FLOAT_VEC3,3,"a_tangent",        3, GL33.GL_INT_2_10_10_10_REV,true),
    BI_NORMAL          (GL20.GL_FLOAT_VEC3,3,"a_biNormal",       3, GL33.GL_INT_2_10_10_10_REV,true),
    BONE_WEIGHT0       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight0",    3, GL11.GL_FLOAT,             false),
    BONE_WEIGHT1       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight1",    3, GL11.GL_FLOAT,             false),
    BONE_WEIGHT2       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight2",    3, GL11.GL_FLOAT,             false),
    BONE_WEIGHT3       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight3",    3, GL11.GL_FLOAT,             false),
    BONE_WEIGHT4       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight4",    3, GL11.GL_FLOAT,             false),
    BONE_WEIGHT5       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight5",    3, GL11.GL_FLOAT,             false),
    ;

    public final int     glslVariableType;
    public final int     dimension;
    public final String  glslVariableName;
    public final int     length;
    public final int     primitiveType;
    public final boolean normalized;
    public final int     bitmask;
    public final int     slot;

    VertexAttribute_old_2(final int glslVariableType, final int dimension, final String glslVariableName, final int length, int primitiveType, boolean normalized) {
        this.glslVariableType = glslVariableType;
        this.dimension = dimension;
        this.glslVariableName = glslVariableName;
        this.primitiveType = primitiveType;
        this.normalized = normalized;
        this.length = length;
        this.bitmask = 0b000001 << ordinal();
        this.slot = ordinal();
    }

    public static int getShaderBitmask(final String[] shaderAttributeNames) {
        int bitmask = 0;

        for (final String shaderAttributeName : shaderAttributeNames) {
            for (VertexAttribute_old_2 vertexAttribute : values()) {
                if (vertexAttribute.glslVariableName.equals(shaderAttributeName)) {
                    bitmask |= vertexAttribute.bitmask;
                    break;
                }
            }
        }

        return bitmask;
    }

    public static int getBitmask(final VertexAttribute_old_2...vertexAttribute2s) {
        int bitmask = 0;
        for (final VertexAttribute_old_2 vertexAttribute2 : vertexAttribute2s) {
            bitmask |= vertexAttribute2.bitmask;
        }
        return bitmask;
    }

    public static int getVertexLength(final VertexAttribute_old_2...attribute_2s) {
        int length = 0;
        for (final VertexAttribute_old_2 attribute_2 : attribute_2s) {
            length += attribute_2.length;
        }
        return length;
    }

}
