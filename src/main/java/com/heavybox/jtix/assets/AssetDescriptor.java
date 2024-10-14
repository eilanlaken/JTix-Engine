package com.heavybox.jtix.assets;

import com.heavybox.jtix.memory.MemoryResource;

import java.io.IOException;
import java.util.Objects;

public class AssetDescriptor {

    public final Class<? extends MemoryResource> type;
    public final String                          path;
    public final long                            size;

    // TODO: add Options here in some way.

    public AssetDescriptor(Class<? extends MemoryResource> type, String path) {
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
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AssetDescriptor)) return false;
        if (this == obj) return true;
        AssetDescriptor otherDescriptor = (AssetDescriptor) obj;
        return Objects.equals(this.path, otherDescriptor.path) && this.type == otherDescriptor.type;
    }

}
