package com.heavybox.jtix.application;

import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryResourceHolder;

import java.util.HashMap;
import java.util.Map;

@Deprecated public abstract class ApplicationScreen implements MemoryResourceHolder {

    protected ApplicationWindow window;

    protected abstract void show();

    protected abstract void refresh();

    protected abstract void hide();

    protected abstract void resize(int width, int height);

    // TODO: find a better way of doing background required tasks before show()ing the scene.
    @Deprecated public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        return new HashMap<>();
    }

}
