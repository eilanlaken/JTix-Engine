package com.heavybox.jtix.assets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.GraphicsUtils;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    private Array<AssetDescriptor> dependencies;
    private String                 yamlString;

    @Override
    public Array<AssetDescriptor> asyncLoad(String path, AssetLoader.Options options) {
        Options packOptions = (Options) options;

        yamlString = AssetUtils.getFileContent(path);
        Yaml yaml = AssetUtils.yaml();
        Map<String, Object> data = yaml.load(yamlString);

        /* get dependencies */
        List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
        dependencies = new Array<>(textures.size());
        for (Map<String, Object> texture : textures) {
            String fileName = (String) texture.get("file");
            Path directoryPath = Paths.get(path).getParent();
            String filePath = Paths.get(directoryPath.toString(), fileName).toString();
            AssetLoaderTexture.Options textureLoaderOptions = new AssetLoaderTexture.Options();
            textureLoaderOptions.anisotropy = packOptions.anisotropy;
            textureLoaderOptions.magFilter = packOptions.magFilter;
            textureLoaderOptions.minFilter = packOptions.minFilter;
            textureLoaderOptions.uWrap = packOptions.uWrap;
            textureLoaderOptions.vWrap = packOptions.vWrap;
            dependencies.add(new AssetDescriptor(Texture.class, filePath, textureLoaderOptions));
        }

        return dependencies;
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

        return new TexturePack(textures, yamlString);
    }

    public static final class Options extends AssetLoader.Options<TexturePack> {

        public int            anisotropy = GraphicsUtils.getMaxAnisotropicFilterLevel();
        public Texture.Filter minFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        public Texture.Filter magFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        public Texture.Wrap   uWrap      = Texture.Wrap.CLAMP_TO_EDGE;
        public Texture.Wrap   vWrap      = Texture.Wrap.CLAMP_TO_EDGE;

    }

}
