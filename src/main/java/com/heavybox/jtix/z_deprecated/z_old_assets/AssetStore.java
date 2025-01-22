package com.heavybox.jtix.z_deprecated.z_old_assets;

import com.heavybox.jtix.async.AsyncTaskRunner;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Queue;
import com.heavybox.jtix.z_deprecated.z_graphics_old.Font;
import com.heavybox.jtix.z_deprecated.z_graphics_old.Model;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.memory.MemoryResource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AssetStore {

    private static final HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> loaders = createLoadersMap();

    private static final HashMap<String, Asset>     store                = new HashMap<>();
    private static final Queue<AssetDescriptor>     storeLoadQueue = new Queue<>();

    private static final Set<AssetStoreLoadingTask> storeAsyncTasks = new HashSet<>();
    private static final Set<AssetStoreLoadingTask> storeCompletedAsyncTasks = new HashSet<>();

    private static final Set<AssetStoreLoadingTask> storeCreateTasks = new HashSet<>();
    private static final Set<AssetStoreLoadingTask> storeCompletedCreateTasks = new HashSet<>();

    // TODO: loading state

    @Deprecated public static synchronized void update() {
        for (AssetStoreLoadingTask task : storeAsyncTasks) {
            if (task.ready())  {
                storeCompletedAsyncTasks.add(task);
                storeCreateTasks.add(task);
            }
        }

        storeAsyncTasks.removeAll(storeCompletedAsyncTasks);
        for (AssetDescriptor descriptor : storeLoadQueue) {
            AssetStoreLoadingTask task = new AssetStoreLoadingTask(descriptor);
            storeAsyncTasks.add(task);
            AsyncTaskRunner.async(task);
        }
        storeLoadQueue.clear();

        storeCreateTasks.removeAll(storeCompletedCreateTasks);
        for (AssetStoreLoadingTask task : storeCreateTasks) {
            Asset asset = task.create();
            store.put(asset.descriptor.path, asset);
            storeCompletedCreateTasks.add(task);
        }
    }

    static synchronized Array<Asset> getDependencies(final Array<AssetDescriptor> dependencies) {
        Array<Asset> assets = new Array<>();
        if (dependencies != null) {
            for (AssetDescriptor dependency : dependencies) {
                assets.add(store.get(dependency.path));
            }
        }
        return assets;
    }

    static synchronized boolean areLoaded(final Array<AssetDescriptor> dependencies) {
        if (dependencies == null || dependencies.size == 0) return true;
        for (AssetDescriptor dependency : dependencies) {
            Asset asset = store.get(dependency.path);
            if (asset == null) return false;
        }
        return true;
    }

    public static synchronized boolean isLoaded(final String path) {
        return store.get(path) != null;
    }

    public static void load(Class<? extends MemoryResource> type, String path) {
        load(type, path, null, false);
    }

    public static void load(Class<? extends MemoryResource> type, String path, AssetLoader.Options<? extends MemoryResource> options) {
        load(type, path, options, false);
    }

    static void load(Class<? extends MemoryResource> type, String path, AssetLoader.Options<? extends MemoryResource> options, boolean isDependency) {
        final Asset asset = store.get(path);
        if (asset != null) {
            if (isDependency) asset.refCount++;
            return;
        }
        if (!AssetUtils.fileExists(path)) throw new AssetException("File not found: " + path);
        AssetDescriptor descriptor = new AssetDescriptor(type, path, options);
        storeLoadQueue.addFirst(descriptor);
    }

    public static synchronized void unload(final String path) {

    }

    public static synchronized <T extends MemoryResource> T get(final String path) {
        var t = store.get(path);
        if (t == null) throw new AssetException("File not loaded: " + path + System.lineSeparator() + "Make sure you spelled the file path correctly. You must " +
                "provide the full relative path.");
        return (T) t.data;
    }

    // TODO
    public static synchronized void clear() {

    }

    public static long getTotalStorageBytes() {
        long total = 0;
        for (Map.Entry<String, Asset> assetEntry : store.entrySet()) {
            total += assetEntry.getValue().descriptor.size;
        }
        return total;
    }

    public static boolean isLoadingInProgress() {
        return !storeLoadQueue.isEmpty() || !storeAsyncTasks.isEmpty() || !storeCreateTasks.isEmpty();
    }

    static synchronized AssetLoader<? extends MemoryResource> getNewLoader(Class<? extends MemoryResource> type) {
        Class<? extends AssetLoader<? extends MemoryResource>> loaderClass = AssetStore.loaders.get(type);
        AssetLoader<? extends MemoryResource> loaderInstance;
        try {
            Constructor<?> constructor = loaderClass.getConstructor();
            loaderInstance = (AssetLoader<? extends MemoryResource>) constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException  | InvocationTargetException e) {
            throw new AssetException("Could not get loader for type: " + type.getSimpleName());
        }
        return loaderInstance;
    }

    private static HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> createLoadersMap() {
        HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> loaders = new HashMap<>();
        loaders.put(Texture.class, AssetLoaderTexture.class);
        loaders.put(Font.class, AssetLoaderFont.class);
        loaders.put(Model.class, AssetLoaderModel.class);
        loaders.put(TexturePack.class, AssetLoaderTexturePack.class);
        return loaders;
    }

}
