package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;

public interface AssetLoader<T> {

    Array<AssetDescriptor> getDependencies();
    void asyncLoad(final String path, final Options<T> options);
    T create();

    T create(Options<T> options);

    // TODO: finalize asset loading.
    // tasks: font loading
    // shader loading
    // parameters
    // asset management, free and delete
    // handle background tasks - texture packing etc together with loading.
    class Options<T> {

    }

}
