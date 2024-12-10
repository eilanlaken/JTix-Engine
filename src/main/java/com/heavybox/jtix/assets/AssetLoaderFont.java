package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.tools.ToolsFontGenerator;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: here is the solution: check if suffix is .ttf or .yaml or yml.
// If ttf generate bitmap, then load the bitmap font
// if bitmap, load the bitmap font.
public class AssetLoaderFont implements AssetLoader<Font> {

    private Array<AssetDescriptor> dependencies;
    private Map<String, Object>    data;

    // TODO: possible generate fonts here.
    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {

        if (options == null && !Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path); // trying to load a bitmap font that does not exist

        if (options != null) {
            String originalFilepath = (String) options.get("originalPath");
            if (!Assets.fileExists(originalFilepath)) throw new AssetsException("File does not exist: " + path); // trying to CREATE a bitmap font from a .ttf file that does not exist

            String filename = Paths.get(originalFilepath).getFileName().toString();
            String filenameNoExtension = Assets.removeExtension(filename);

            int size = (int) options.get("size");
            boolean antialiasing = (boolean) options.get("antialiasing");
            String charset = (String) options.get("charset");

            Path font = Paths.get(path);
            Path directory = font.getParent();
            String newFile = filenameNoExtension + "-" + size + ".yml";
            String newFilepath = directory.resolve(newFile).toString();
            options.put("newFilepath", newFilepath);

            ToolsFontGenerator.generateFontBitmap(originalFilepath, size, antialiasing, charset);
        }
    }

    @Override
    public Array<AssetDescriptor> load(final String path, final HashMap<String, Object> options) {
        String contentPath;
        if (options == null) {
            contentPath = path;
        } else {
            contentPath = (String) options.get("newFilepath");
        }
        String yaml = Assets.getFileContent(contentPath);
        data = Assets.yaml().load(yaml);

        Map<String, Object> meta = (Map<String, Object>) data.get("meta");
        String directoryPath = Paths.get(path).getParent().toString();
        String atlasName = (String) meta.get("atlas");

        String filePath = Paths.get(directoryPath, atlasName).toString();
        dependencies = new Array<>(false, 1);

        HashMap<String, Object> textureLoadingOptions = new HashMap<>();
        textureLoadingOptions.put("anisotropy", 0);
        textureLoadingOptions.put("minFilter", Texture.FilterMin.NEAREST_MIPMAP_LINEAR);
        dependencies.add(new AssetDescriptor(Texture.class, filePath, textureLoadingOptions));
        return dependencies;
    }

    @Override
    public Font afterLoad() {
        String path = dependencies.first().filepath;
        Map<String, Object> options = (Map<String, Object>) data.get("options");
        Texture atlas = Assets.get(path);
        String charset = (String) options.get("charset");
        int size = (int) options.get("size");
        boolean antialiasing = (boolean) options.get("antialiasing");
        Map<Integer, Font.Glyph> glyphs = new HashMap<>();

        List<Map<String, Object>> glyphsData = (List<Map<String, Object>>) data.get("glyphs");
        // Loop through the list of glyphs and print their data
        for (Map<String, Object> glyphData : glyphsData) {

            float advanceX = ((Double) glyphData.get("advanceX")).floatValue();
            float advanceY = ((Double) glyphData.get("advanceY")).floatValue();
            int atlasX = (int) glyphData.get("atlasX");
            int atlasY = (int) glyphData.get("atlasY");
            float bearingX = ((Double) glyphData.get("bearingX")).floatValue();
            float bearingY = ((Double) glyphData.get("bearingY")).floatValue();
            int character = (int) glyphData.get("character");
            int height = (int) glyphData.get("height");
            int width = (int) glyphData.get("width");
            Map<Character, Integer> kernings = (Map<Character, Integer>) glyphData.get("kernings");

            Font.Glyph glyph = new Font.Glyph(width, height, bearingX, bearingY, advanceX, advanceY, atlasX, atlasY, kernings);
            glyphs.put(character, glyph);
        }

        return new Font(atlas, charset, size, antialiasing, glyphs);
    }

}
