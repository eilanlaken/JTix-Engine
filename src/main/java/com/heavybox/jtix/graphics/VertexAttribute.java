package com.heavybox.jtix.graphics;

import org.lwjgl.opengl.GL20;

public enum VertexAttribute {

    POSITION_2D        (GL20.GL_FLOAT_VEC2,2,"a_position"   ),
    POSITION_3D        (GL20.GL_FLOAT_VEC3,3,"a_position"   ),
    COLOR              (GL20.GL_FLOAT_VEC4,4,"a_color"      ),
    TEXT_COORDS0       (GL20.GL_FLOAT_VEC2,2,"a_textCoords0"),
    TEXT_COORDS1       (GL20.GL_FLOAT_VEC2,2,"a_textCoords1"),
    NORMAL_2D          (GL20.GL_FLOAT_VEC2,2,"a_normal"     ),
    NORMAL_3D          (GL20.GL_FLOAT_VEC3,3,"a_normal"     ),
    TANGENT_2D         (GL20.GL_FLOAT_VEC2,2,"a_tangent"    ),
    TANGENT_3D         (GL20.GL_FLOAT_VEC3,3,"a_tangent"    ),
    BI_NORMAL_3D       (GL20.GL_FLOAT_VEC3,3,"a_biNormal"   ),
    BONE_WEIGHT0       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight0"),
    BONE_WEIGHT1       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight1"),
    BONE_WEIGHT2       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight2"),
    BONE_WEIGHT3       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight3"),
    BONE_WEIGHT4       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight4"),
    BONE_WEIGHT5       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight5"),
    ;

    public final int     glslVariableType;
    public final int     dimension;
    public final String  glslVariableName;
    public final int     bitmask;
    public final int     slot;

    VertexAttribute(final int glslVariableType, final int dimension, final String glslVariableName) {
        this.glslVariableType = glslVariableType;
        this.dimension = dimension;
        this.glslVariableName = glslVariableName;
        this.bitmask = 0b000001 << ordinal();
        this.slot = ordinal();
    }

    public static int getShaderBitmask(final String[] shaderAttributeNames) {
        int bitmask = 0;

        for (final String shaderAttributeName : shaderAttributeNames) {
            for (VertexAttribute vertexAttribute : values()) {
                if (vertexAttribute.glslVariableName.equals(shaderAttributeName)) {
                    bitmask |= vertexAttribute.bitmask;
                    break;
                }
            }
        }

        return bitmask;
    }

    public static int getBitmask(final VertexAttribute...vertexAttribute2s) {
        int bitmask = 0;
        for (final VertexAttribute vertexAttribute2 : vertexAttribute2s) {
            bitmask |= vertexAttribute2.bitmask;
        }
        return bitmask;
    }

}
