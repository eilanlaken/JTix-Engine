package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    private Array<AssetDescriptor> dependencies;
    private String                 yaml;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void asyncLoad(String path) {

        yaml = AssetUtils.getFileContent(path);
        Map<String, Object> data = AssetUtils.yaml.load(yaml);

        /* get dependencies */
        List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
        dependencies = new Array<>(textures.size());
        for (Map<String, Object> texture : textures) {
            String fileName = (String) texture.get("file");
            Path directoryPath = Paths.get(path).getParent();
            String filePath = Paths.get(directoryPath.toString(), fileName).toString();
            dependencies.add(new AssetDescriptor(Texture.class, filePath));
        }
    }

    @Override
    public TexturePack create() {
        /* get Textures */
        Texture[] textures = new Texture[dependencies.size];
        for (int i = 0; i < textures.length; i++) {
            String path = dependencies.get(i).path;
            Texture texture = AssetStore.get(path);
            textures[i] = texture;
        }

        return new TexturePack(textures, yaml);
    }

}
