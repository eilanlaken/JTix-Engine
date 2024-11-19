package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class AssetLoaderTexture implements AssetLoader<Texture> {

    private int        width;
    private int        height;
    private ByteBuffer buffer;
    private HashMap<String, Object> options;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, final HashMap<String, Object> options) {
        this.options = options;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new AssetsException("Failed to load a texture file. Check that the path is correct: " + path
                    + System.lineSeparator() + "STBImage error: "
                    + STBImage.stbi_failure_reason());
            width = widthBuffer.get();
            height = heightBuffer.get();
            int maxTextureSize = Graphics.getMaxTextureSize();
            if (width > maxTextureSize || height > maxTextureSize) throw new IllegalStateException("Trying to load texture " + path + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);
        }
        return null;
    }

    @Override
    public Texture create() {
        final int anisotropy = options == null ? Graphics.getMaxAnisotropy() : (int) options.get("anisotropy");
        final Texture.FilterMag magFilter = options == null ? null : (Texture.FilterMag) options.get("magFilter");
        final Texture.FilterMin minFilter = options == null ? null : (Texture.FilterMin) options.get("minFilter");
        final Texture.Wrap uWrap = options == null ? null : (Texture.Wrap) options.get("uWrap");
        final Texture.Wrap vWrap = options == null ? null : (Texture.Wrap) options.get("vWrap");
        Texture texture = new Texture(width, height, buffer, magFilter, minFilter, uWrap, vWrap, anisotropy);
        STBImage.stbi_image_free(buffer);
        return texture;
    }

//    public static final class Options extends AssetLoader.Options<Texture> {
//
//        public int            anisotropy = GraphicsUtils.getMaxAnisotropicFilterLevel();
//        public Texture.Filter minFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
//        public Texture.Filter magFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
//        public Texture.Wrap   uWrap      = Texture.Wrap.CLAMP_TO_EDGE;
//        public Texture.Wrap   vWrap      = Texture.Wrap.CLAMP_TO_EDGE;
//
//    }

}