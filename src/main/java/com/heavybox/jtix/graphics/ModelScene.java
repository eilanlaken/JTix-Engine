package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryResource;

public class ModelScene implements MemoryResource {

    public ModelMesh[] allMeshes;
    public ModelMaterial[] allMaterials;
    public Matrix4x4 allTransforms;

    @Override
    public void delete() {
        // TODO
    }

    public static class Node {

        public String name;
        public Matrix4x4 transform;
        public ModelMesh[]     meshes;
        public ModelMaterial[] materials;
        public Node[] children;

    }

}
