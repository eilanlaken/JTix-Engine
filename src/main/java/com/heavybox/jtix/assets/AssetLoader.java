package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;

interface AssetLoader<T extends MemoryResource> {

    Array<AssetDescriptor> asyncLoad(final String path, final HashMap<String, Object> options);
    T create();

}
