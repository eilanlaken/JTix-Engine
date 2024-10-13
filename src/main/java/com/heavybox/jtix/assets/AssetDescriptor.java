package com.heavybox.jtix.assets;

import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class AssetDescriptor<T extends MemoryResource> {

    public final Class<T>               type;
    public final String                 path;
    public final long                   size;
    public final AssetLoader.Options<T> options;

    public AssetDescriptor(Class<T> type, String path, @Nullable AssetLoader.Options<T> options) {
        this.type = type;
        this.path = path;
        long s = 0;
        try {
            s = AssetUtils.getFileSize(path);
        } catch (IOException e) {
            // TODO: see how to get the total file size for assets that are composed of multiple files.
            //throw new AssetsException(e.getMessage());
        }
        this.size = s;
        this.options = options;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AssetDescriptor)) return false;
        if (this == obj) return true;
        AssetDescriptor<T> otherDescriptor;
        try {
            otherDescriptor = (AssetDescriptor<T>) obj;
        } catch (Exception e) {
            return false;
        }
        return Objects.equals(this.path, otherDescriptor.path) && this.type == otherDescriptor.type && Objects.equals(this.options, otherDescriptor.options);
    }

}
