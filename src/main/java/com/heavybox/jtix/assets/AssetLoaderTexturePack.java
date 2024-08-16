package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    private Array<AssetDescriptor> dependencies;
    private String yml;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        try {
            String content = AssetUtils.getFileContent(path);
            FileInputStream inputStream = new FileInputStream(path);
            Map<String, Object> data = AssetUtils.yaml.load(inputStream);
            List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
            dependencies = new Array<>(textures.size());
            for (Map<String, Object> texture : textures) {
                String fileName = (String) texture.get("file");
                Path directoryPath = Paths.get(path).getParent();
                String filePath = Paths.get(directoryPath.toString(), fileName).toString();
                System.out.println(content);
                dependencies.add(new AssetDescriptor(Texture.class, filePath));
            }
        } catch (FileNotFoundException e) {
            throw new AssetsException("File not found: " + e.getMessage());
        }
    }

    @Override
    public TexturePack create() {
        System.out.println(yml);
        return null;
    }

}
