package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {


    private Array<AssetDescriptor> dependencies;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            Map<String, Object> data = AssetUtils.yaml.load(inputStream);
            List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
            dependencies = new Array<>(textures.size());
            for (Map<String, Object> texture : textures) {
                String file = (String) texture.get("file");
                dependencies.add(new AssetDescriptor(Texture.class, file));
            }
        } catch (FileNotFoundException e) {
            throw new AssetsException("File not found: " + e.getMessage());
        }
    }

    @Override
    public TexturePack create() {
        return null;
    }

}
