package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;

public class AssetLoaderString implements AssetLoader<String> {

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public String create() {
        return null;
    }
}
