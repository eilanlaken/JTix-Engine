package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.freetype.FT_Face;

public class AssetLoaderFont implements AssetLoader<Font> {

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {
    }

    @Override
    public Font create() {
        return null;
    }

}
