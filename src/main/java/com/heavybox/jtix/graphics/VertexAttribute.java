package com.heavybox.jtix.graphics;

import org.lwjgl.opengl.GL20;

public enum VertexAttribute {

    POSITION_2D        (GL20.GL_FLOAT_VEC2,2,"a_position"   , 0),
    POSITION_3D        (GL20.GL_FLOAT_VEC3,3,"a_position"   , 0),
    COLOR              (GL20.GL_FLOAT_VEC4,4,"a_color"      , 1),
    TEXT_COORDS0       (GL20.GL_FLOAT_VEC2,2,"a_textCoords0", 2),
    TEXT_COORDS1       (GL20.GL_FLOAT_VEC2,2,"a_textCoords1", 3),
    NORMAL_2D          (GL20.GL_FLOAT_VEC2,2,"a_normal"     , 4),
    NORMAL_3D          (GL20.GL_FLOAT_VEC3,3,"a_normal"     , 4),
    TANGENT_2D         (GL20.GL_FLOAT_VEC2,2,"a_tangent"    , 5),
    TANGENT_3D         (GL20.GL_FLOAT_VEC3,3,"a_tangent"    , 5),
    BI_NORMAL_3D       (GL20.GL_FLOAT_VEC3,3,"a_biNormal"   , 6),
    BONE_WEIGHT0       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight0", 7),
    BONE_WEIGHT1       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight1", 8),
    BONE_WEIGHT2       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight2", 9),
    BONE_WEIGHT3       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight3", 10),
    BONE_WEIGHT4       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight4", 11),
    BONE_WEIGHT5       (GL20.GL_FLOAT_VEC3,2,"a_boneWeight5", 12),
    ;

    public final int     glslVariableType;
    public final int     dimension;
    public final String  glslVariableName;
    public final int     glslLocation;
    public final int     bitmask;

    VertexAttribute(final int glslVariableType, final int dimension, final String glslVariableName, final int glslLocation) {
        this.glslVariableType = glslVariableType;
        this.dimension = dimension;
        this.glslVariableName = glslVariableName;
        this.glslLocation = glslLocation;
        this.bitmask = 0b000001 << glslLocation;
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

    public static int getBitmask(final VertexAttribute...vertexAttributes) {
        int bitmask = 0;
        for (final VertexAttribute vertexAttribute : vertexAttributes) {
            bitmask |= vertexAttribute.bitmask;
        }
        return bitmask;
    }

    public static int getAttributeLocation(final String name) {
        for (VertexAttribute attribute : values()) {
            if (attribute.glslVariableName.equals(name)) return attribute.glslLocation;
        }
        return -1;
    }

}
