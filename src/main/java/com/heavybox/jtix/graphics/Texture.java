package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// TODO:
// need to change filter
// need to put anisotropy in Graphics
// need to implement LOD: GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
public final class Texture implements MemoryResource {

    private       int       handle;
    private       int       slot;
    public  final int       width;
    public  final int       height;
    public  final float     invWidth;
    public  final float     invHeight;
    public  final FilterMag filterMag;
    public  final FilterMin filterMin;
    public  final Wrap      sWrap;
    public  final Wrap      tWrap;
    private       int       anisotropy;
    private       float     biasLOD; // higher LOD bias will sample from higher mip level, which means lower texture quality.

    private ByteBuffer bytes;

    public Texture(int width, int height, ByteBuffer bytes, FilterMag filterMag, FilterMin filterMin, Wrap sWrap, Wrap tWrap, int anisotropy) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new IllegalStateException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = filterMag != null ? filterMag : FilterMag.NEAREST;
        this.filterMin = filterMin != null ? filterMin : FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = sWrap != null ? sWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = tWrap != null ? tWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = MathUtils.nextPowerOfTwo(MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy()));
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        if (filterMin == FilterMin.NEAREST_MIPMAP_LINEAR ||
                filterMin == FilterMin.LINEAR_MIPMAP_LINEAR  ||
                filterMin == FilterMin.LINEAR_MIPMAP_NEAREST ||
                filterMin == FilterMin.NEAREST_MIPMAP_NEAREST) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
        } else {
            this.anisotropy = 1;
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        }

//        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
//        if (Graphics.isAnisotropicFilteringSupported()) {
//            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
//        }
    }

    // TODO: test
    public Texture(final String path, FilterMag filterMag, FilterMin filterMin, Wrap sWrap, Wrap tWrap, int anisotropy) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
        ByteBuffer buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
        if (buffer == null) throw new RuntimeException("Failed to load a texture file. Check that the path is correct: " + path
                + System.lineSeparator() + "STBImage error: "
                + STBImage.stbi_failure_reason());
        this.width = widthBuffer.get();
        this.height = heightBuffer.get();
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;
        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new IllegalStateException("Trying to load texture " + path + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.filterMag = filterMag != null ? filterMag : FilterMag.NEAREST;
        this.filterMin = filterMin != null ? filterMin : FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = sWrap != null ? sWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = tWrap != null ? tWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        if (filterMin == FilterMin.NEAREST_MIPMAP_LINEAR ||
            filterMin == FilterMin.LINEAR_MIPMAP_LINEAR  ||
            filterMin == FilterMin.LINEAR_MIPMAP_NEAREST ||
            filterMin == FilterMin.NEAREST_MIPMAP_NEAREST) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
        } else {
            this.anisotropy = 1;
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        }

//        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
//        if (Graphics.isAnisotropicFilteringSupported()) {
//            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
//        }

        STBImage.stbi_image_free(buffer); // TODO: test
    }

    void setSlot(final int slot) { this.slot = slot; }
    int  getSlot() { return slot; }
    int  getHandle() { return handle; }

    public int getAnisotropy() {
        return anisotropy;
    }

    public float getBiasLOD() {
        return biasLOD;
    }

    public void setAnisotropy(int anisotropy) {
        if (this.anisotropy == anisotropy) return;
        this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
        TextureBinder.bind(this);
        GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
        if (Graphics.isAnisotropicFilteringSupported()) GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
    }

    public void setBiasLOD(float biasLOD) {
        if (this.biasLOD == biasLOD) return;
        this.biasLOD = biasLOD;
        TextureBinder.bind(this);
        GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, this.biasLOD);
    }

    public Color getPixelColor(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IndexOutOfBoundsException("Trying to read out of bounds pixel: (" + x + ", " + y + ") of " + Texture.class.getSimpleName() + " with dimensions: " + "(" + width + ", " + height + ")");

        if (bytes == null) {
            bytes = BufferUtils.createByteBuffer(width * height * 4);
            int slot = TextureBinder.bind(this);
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        }

        int index = (x + y * width) * 4;
        int r = bytes.get(index + 0) & 0xFF;
        int g = bytes.get(index + 1) & 0xFF;
        int b = bytes.get(index + 2) & 0xFF;
        int a = bytes.get(index + 3) & 0xFF;
        Color.WHITE.toFloatBits();
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    @Override
    public void delete() {
        TextureBinder.unbind(this);
        GL11.glDeleteTextures(handle);
        handle = -1;
    }

    public enum FilterMag {

        NEAREST (GL20.GL_NEAREST),
        LINEAR  (GL20.GL_LINEAR),
        ;

        public final int glValue;

        FilterMag(final int glValue) {
            this.glValue = glValue;
        }
    }

    public enum FilterMin {

        NEAREST                (GL20.GL_NEAREST),
        LINEAR                 (GL20.GL_LINEAR),
        NEAREST_MIPMAP_NEAREST (GL20.GL_NEAREST_MIPMAP_NEAREST),
        LINEAR_MIPMAP_NEAREST  (GL20.GL_LINEAR_MIPMAP_NEAREST),
        NEAREST_MIPMAP_LINEAR  (GL20.GL_NEAREST_MIPMAP_LINEAR),
        LINEAR_MIPMAP_LINEAR   (GL20.GL_LINEAR_MIPMAP_LINEAR)
        ;

        public final int glValue;

        FilterMin(final int glValue) {
            this.glValue = glValue;
        }

    }

    public enum Wrap {

        MIRRORED_REPEAT(GL20.GL_MIRRORED_REPEAT),
        CLAMP_TO_EDGE(GL20.GL_CLAMP_TO_EDGE),
        REPEAT(GL20.GL_REPEAT)
        ;

        public final int glValue;

        Wrap(int glValue) {
            this.glValue = glValue;
        }

    }
}
