package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.CollectionsUtils;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Deprecated public final class TextureBuilder {

    public static final int maxTextureSize = GraphicsUtils.getMaxTextureSize();

    private TextureBuilder() {}

    public static Texture buildTextureFromFilePath(final String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            final ByteBuffer buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new GraphicsException("Failed to load a texture file. Check that the path is correct: " + path + System.lineSeparator() + "STBImage error: " + STBImage.stbi_failure_reason());
            final int width = widthBuffer.get();
            final int height = heightBuffer.get();
            if (width > maxTextureSize || height > maxTextureSize) throw new GraphicsException("Trying to load texture " + path + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);
            Texture texture = buildTextureFromByteBuffer(width, height, buffer, null, null, null, null);
            STBImage.stbi_image_free(buffer);
            return texture;
        }
    }

    public static Texture buildTextureFromClassPath(final String name) {
        ByteBuffer imageBuffer;

        // Load the image resource into a ByteBuffer
        try (InputStream is = TextureBuilder.class.getClassLoader().getResourceAsStream(name);
             ReadableByteChannel rbc = Channels.newChannel(is)) {
            imageBuffer = BufferUtils.createByteBuffer(1024);

            while (true) {
                int bytes = rbc.read(imageBuffer);
                if (bytes == -1) {
                    break;
                }
                if (imageBuffer.remaining() == 0) {
                    imageBuffer = MemoryUtils.resizeBuffer(imageBuffer, imageBuffer.capacity() * 2);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imageBuffer.flip(); // Flip the buffer for reading
        int[] width = new int[1];
        int[] height = new int[1];
        int[] comp = new int[1];

        ByteBuffer buffer = STBImage.stbi_load_from_memory(imageBuffer, width, height, comp,4);
        if (buffer == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        Texture texture = buildTextureFromByteBuffer(width[0], height[0], buffer, null, null, null, null);
        STBImage.stbi_image_free(buffer);
        return texture;
    }

    public static Texture buildTextureFromByteBuffer(int width, int height, ByteBuffer buffer, Texture.Filter magFilter, Texture.Filter minFilter, Texture.Wrap uWrap, Texture.Wrap vWrap) {
        if (magFilter == null) magFilter = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        if (minFilter == null) minFilter = Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        if (uWrap == null) uWrap = Texture.Wrap.CLAMP_TO_EDGE;
        if (vWrap == null) vWrap = Texture.Wrap.CLAMP_TO_EDGE;

        int glHandle = GL11.glGenTextures();

        Texture texture = new Texture(glHandle, width, height, magFilter, minFilter, uWrap, vWrap);
        TextureBinder.bind(texture);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // TODO: here we need to see if we want to: generate mipmaps, use anisotropic filtering, what level of anisotropy etc
        // TODO: For a raw Texture with no TextureMap, use defaults.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // TODO: we need to see if the anisotropic filtering extension is available. If yes, create that instead of mipmaps.

        return texture;
    }

}



