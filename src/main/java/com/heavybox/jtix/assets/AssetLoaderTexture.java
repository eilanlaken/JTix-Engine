package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.GraphicsUtils;
import com.heavybox.jtix.graphics.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class AssetLoaderTexture implements AssetLoader<Texture> {

    private int        width;
    private int        height;
    private ByteBuffer buffer;

    @Override
    public void asyncLoad(String path) {
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
            int maxTextureSize = GraphicsUtils.getMaxTextureSize();
            if (width > maxTextureSize || height > maxTextureSize)
                throw new IllegalStateException("Trying to load texture " + path + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);
        }
    }

    @Override
    public Texture create() {
        Texture texture = new Texture(width, height, buffer, null, null, null, null, 16);
        STBImage.stbi_image_free(buffer);
        return texture;
    }

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    public static final class Options extends AssetLoader.Options<Texture> {

        public int            anisotropy = GraphicsUtils.getMaxAnisotropicFilterLevel();
        public Texture.Filter minFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        public Texture.Filter magFilter  = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        public Texture.Wrap   uWrap      = Texture.Wrap.CLAMP_TO_EDGE;
        public Texture.Wrap   vWrap      = Texture.Wrap.CLAMP_TO_EDGE;

    }

}