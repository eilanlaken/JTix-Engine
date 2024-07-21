package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;

public interface AssetLoader<T> {

    Array<AssetDescriptor> getDependencies();
    void asyncLoad(final String path);
    T create();

}
