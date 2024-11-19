package com.heavybox.jtix.assets;

import com.heavybox.jtix.async.AsyncTask;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

public class AssetLoadingTask extends AsyncTask {

    private final AssetDescriptor descriptor;
    private Array<AssetDescriptor> dependencies;
    private final AssetLoader<? extends MemoryResource> loader;

    AssetLoadingTask(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = Assets.getNewLoader(descriptor.type);
    }

    @Override
    public void task() {
        this.dependencies = loader.backgroundLoad(descriptor.filepath, descriptor.options);
    }

    @Override
    public void onComplete() {
        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) Assets.load(dependency.type, dependency.filepath, dependency.options,true);
    }

    protected boolean ready() {
        if (!isComplete()) return false;
        if (dependencies == null || dependencies.size == 0) return true;
        return Assets.areLoaded(dependencies);
    }

    protected Asset create() {
        final MemoryResource data = loader.create();
        final Array<Asset> assetDependencies = Assets.getDependencies(dependencies);
        return new Asset(data, descriptor, assetDependencies);
    }

}
