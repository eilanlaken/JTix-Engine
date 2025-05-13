package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;
import java.util.Map;


// TODO: make clone()able
public class ModelMaterial implements MemoryResource {

    public Shader shader = null;
    public boolean useLights = true;
    public boolean transparent = false;
    public HashMap<String, Object> materialAttributes = new HashMap<>();

    @Override
    public void delete() {
        for (Map.Entry<String, Object> attribute : materialAttributes.entrySet()) {
            Object data = attribute.getValue();
            if (data instanceof MemoryResource) { // this will effectively delete all the textures.
                MemoryResource resource = (MemoryResource) data;
                resource.delete();
            }
        }
    }

    @Override
    public String toString() {
        return "ModelMaterial{" +
                "materialAttributes=" + materialAttributes +
                '}';
    }
}
