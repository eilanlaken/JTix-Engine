package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;

interface AssetLoader<T extends MemoryResource> {

    void beforeLoad(final String path, final HashMap<String, Object> options);
    Array<AssetDescriptor> backgroundLoad(final String path, final HashMap<String, Object> options);
    T create();

}
