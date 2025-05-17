package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryResource;

public class ModelScene implements MemoryResource {

    public ModelMesh[] allMeshes;
    public ModelMaterial[] allMaterials;
    public Array<Node> nodes = new Array<>();

    @Override
    public void delete() {
        // TODO
    }

    public static class Node {

        // TODO: add parent. The node hierarchy should be a tree, not a list.
        public Node parent;
        public String name;
        public Matrix4x4 localTransform;
        public Model model;
        public Node[] children;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("meshes: ").append(allMeshes.length).append('\n');
        sb.append("materials: ").append(allMaterials.length).append('\n');
        sb.append("nodes: ").append(nodes.size).append('\n');
        for (Node node : nodes) {
            sb.append("---\n");
            sb.append(node.name).append('\n');
            sb.append(node.localTransform).append('\n');
        }
        return sb.toString();
    }
}
