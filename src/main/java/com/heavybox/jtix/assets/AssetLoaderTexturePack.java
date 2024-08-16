package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TexturePacker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    private Array<AssetDescriptor> dependencies;
    private TexturePacker.Options options;
    private Array<TextureRegionData> regionsData;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            Map<String, Object> data = AssetUtils.yaml.load(inputStream);

            /* get texture names */
            List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
            dependencies = new Array<>(textures.size());
            for (Map<String, Object> texture : textures) {
                String fileName = (String) texture.get("file");
                Path directoryPath = Paths.get(path).getParent();
                String filePath = Paths.get(directoryPath.toString(), fileName).toString();
                dependencies.add(new AssetDescriptor(Texture.class, filePath));
            }

            /* get options */
            Map<String, Object> optionsMap = (Map<String, Object>) data.get("options");
            options = new TexturePacker.Options(
                    (String) optionsMap.get("outputDirectory"),
                    (String) optionsMap.get("outputName"),
                    Texture.Filter.valueOf((String) optionsMap.get("magFilter")),
                    Texture.Filter.valueOf((String) optionsMap.get("minFilter")),
                    Texture.Wrap.valueOf((String) optionsMap.get("uWrap")),
                    Texture.Wrap.valueOf((String) optionsMap.get("vWrap")),
                    (int) optionsMap.get("extrude"),
                    (int) optionsMap.get("padding"),
                    TexturePacker.Options.Size.get((Integer) optionsMap.get("maxTexturesSize"))
            );

            /* get texture regions */
            List<Map<String, Object>> regions = (List<Map<String, Object>>) data.get("regions");
            regionsData = new Array<>(true, regions.size());
            for (Map<String, Object> region : regions) {
                TextureRegionData regionData = new TextureRegionData();
                regionData.name = (String) region.get("name");
                regionData.offsetX = (int) region.get("offsetX");
                regionData.offsetY = (int) region.get("offsetY");
                regionData.originalWidth = (int) region.get("originalWidth");
                regionData.originalHeight = (int) region.get("originalHeight");
                regionData.packedWidth = (int) region.get("packedWidth");
                regionData.packedHeight = (int) region.get("packedHeight");
                regionData.textureIndex = (int) region.get("textureIndex");
                regionData.x = (int) region.get("x");
                regionData.y = (int) region.get("y");
                regionsData.add(regionData);
            }
        } catch (FileNotFoundException e) {
            throw new AssetsException("File not found: " + e.getMessage());
        }
    }

    @Override
    public TexturePack create() {
        Texture t = AssetStore.get(dependencies.get(0).path);
        if (t == null) System.out.println("null");
        for (TextureRegionData d : regionsData) {
            System.out.println(d.name);
        }
        return null;
    }

    private static class TextureRegionData {

        String name;
        int offsetX;
        int offsetY;
        int originalWidth;
        int originalHeight;
        int packedWidth;
        int packedHeight;
        int textureIndex;
        int x;
        int y;

    }

}
