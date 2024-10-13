package com.heavybox.jtix.assets;

import com.heavybox.jtix.async.AsyncTask;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

public class AssetStoreLoadingTask<T extends MemoryResource> extends AsyncTask {

    private final AssetDescriptor<T> descriptor;
    private Array<AssetDescriptor> dependencies;
    private final AssetLoader<T> loader;

    AssetStoreLoadingTask(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
    }

    @Override
    public void task() {
        loader.asyncLoad(descriptor.path, descriptor.options);
        this.dependencies = loader.getDependencies();
    }

    @Override
    public void onComplete() {
        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) AssetStore.load(dependency.type, dependency.path, dependency.options);
    }

    protected boolean ready() {
        if (!isComplete()) return false;
        if (dependencies == null || dependencies.size == 0) return true;
        return AssetStore.areLoaded(dependencies);
    }

    protected Asset create() {
        final Object obj = loader.create();
        final Array<Asset> assetDependencies = AssetStore.getDependencies(dependencies);
        return new Asset(obj, descriptor, assetDependencies);
    }
}
