package com.heavybox.jtix.z_deprecated.z_old_assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

public interface AssetLoader<T extends MemoryResource> {

    //Array<AssetDescriptor> getDependencies();
    //@Deprecated void asyncLoad(final String path);

    Array<AssetDescriptor> asyncLoad(final String path, final Options options);
    T create();

    class Options<T> {

    }

}
