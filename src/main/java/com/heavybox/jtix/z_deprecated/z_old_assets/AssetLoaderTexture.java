package com.heavybox.jtix.z_deprecated.z_old_assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class AssetLoaderTexture implements AssetLoader<Texture> {

    private int        width;
    private int        height;
    private ByteBuffer buffer;
    private Options    textureOptions;

    @Override
    public Array<AssetDescriptor> asyncLoad(String path, AssetLoader.Options options) {
        textureOptions = (Options) options;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new RuntimeException("Failed to load a texture file. Check that the path is correct: " + path
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
        final int anisotropy = textureOptions == null ? 16 : textureOptions.anisotropy;
        //final Texture.Filter magFilter = textureOptions == null ? null : textureOptions.magFilter;
        //final Texture.Filter minFilter = textureOptions == null ? null : textureOptions.minFilter;
        final Texture.Wrap uWrap = textureOptions == null ? null : textureOptions.uWrap;
        final Texture.Wrap vWrap = textureOptions == null ? null : textureOptions.vWrap;
        Texture texture = new Texture(width, height, buffer, null, null, uWrap, vWrap, anisotropy);
        STBImage.stbi_image_free(buffer);
        return texture;
    }

    public static final class Options extends AssetLoader.Options<Texture> {

        public int            anisotropy = Graphics.getMaxAnisotropy();
        public Texture.Wrap   uWrap      = Texture.Wrap.CLAMP_TO_EDGE;
        public Texture.Wrap   vWrap      = Texture.Wrap.CLAMP_TO_EDGE;

    }

}