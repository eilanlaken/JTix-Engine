package com.heavybox.jtix.z_deprecated.z_old_application;

import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryResourceHolder;

import java.util.HashMap;
import java.util.Map;

@Deprecated public abstract class ApplicationScreen implements MemoryResourceHolder {

    public ApplicationWindow window;

    public abstract void show();

    public abstract void refresh();

    public abstract void hide();

    public abstract void resize(int width, int height);

    // TODO: find a better way of doing background required tasks before show()ing the scene.
    @Deprecated public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        return new HashMap<>();
    }

}
