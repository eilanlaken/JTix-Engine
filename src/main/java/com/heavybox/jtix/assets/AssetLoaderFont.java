package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.tools.FontGenerator;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.freetype.FT_Face;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssetLoaderFont implements AssetLoader<Font> {

    private Array<AssetDescriptor> dependencies;
    private Map<String, Object>    data;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        String yaml = AssetUtils.getFileContent(path);
        data = AssetUtils.yaml().load(yaml);

        Map<String, Object> meta = (Map<String, Object>) data.get("meta");
        String directoryPath = Paths.get(path).getParent().toString();
        String atlasName = (String) meta.get("atlas");

        String filePath = Paths.get(directoryPath, atlasName).toString();
        dependencies = new Array<>(false, 1);
        dependencies.add(new AssetDescriptor(Texture.class, filePath));
    }

    @Override
    public Font create() {
        String path = dependencies.first().path;
        Map<String, Object> options = (Map<String, Object>) data.get("options");
        Texture atlas = AssetStore.get(path);
        String charset = (String) options.get("charset");
        int size = (int) options.get("size");
        boolean antialiasing = (boolean) options.get("antialiasing");
        Map<Integer, Font.Glyph> glyphs = new HashMap<>();

        List<Map<String, Object>> glyphsData = (List<Map<String, Object>>) data.get("glyphs");
        // Loop through the list of glyphs and print their data
        for (Map<String, Object> glyphData : glyphsData) {
            float advanceX = (float) glyphData.get("advanceX");
            float advanceY = (float) glyphData.get("advanceY");
            int atlasX = (int) glyphData.get("atlasX");
            int atlasY = (int) glyphData.get("atlasY");
            float bearingX = (float) glyphData.get("bearingX");
            float bearingY = (float) glyphData.get("bearingY");
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
