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
    private Map<String, Object>    data;


    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        data = AssetUtils.yaml.load(inputStream);

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

        /* get options */
        Map<String, Object> optionsMap = (Map<String, Object>) data.get("options");
        int extrude = (int) optionsMap.get("extrude");
        int padding = (int) optionsMap.get("padding");
        int size = (int) optionsMap.get("maxTexturesSize");

        /* get texture regions */
        List<Map<String, Object>> regions = (List<Map<String, Object>>) data.get("regions");
        HashMap<String, TextureRegion> namedRegions = new HashMap<>();
        for (Map<String, Object> regionData : regions) {
            String name = (String) regionData.get("name");
            Texture texture = textures[(int) regionData.get("textureIndex")];

            int offsetX = (int) regionData.get("offsetX");
            int offsetY = (int) regionData.get("offsetY");
            int originalWidth = (int) regionData.get("originalWidth");
            int originalHeight = (int) regionData.get("originalHeight");
            int packedWidth = (int) regionData.get("packedWidth");
            int packedHeight = (int) regionData.get("packedHeight");
            int x = (int) regionData.get("x");
            int y = (int) regionData.get("y");

            TextureRegion region = new TextureRegion(texture, x, y, offsetX, offsetY, packedWidth, packedHeight, originalWidth, originalHeight);
            namedRegions.put(name, region);
        }

        return new TexturePack(textures, extrude, padding, size, namedRegions);
    }

}
