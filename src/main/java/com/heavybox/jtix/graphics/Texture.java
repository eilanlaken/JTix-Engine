package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

public class Texture implements MemoryResource {

    protected    int    handle;
    private      int    slot;
    public final int    width;
    public final int    height;
    public final float  invWidth;
    public final float  invHeight;
    public final Filter magFilter;
    public final Filter minFilter;
    public final Wrap   uWrap;
    public final Wrap   vWrap;
    public final int    anisotropicFilteringLevel;

    private ByteBuffer bytes;

    public Texture(int width, int height, ByteBuffer bytes, Filter magFilter, Filter minFilter, Wrap uWrap, Wrap vWrap, int anisotropicFilteringLevel) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.magFilter = magFilter != null ? magFilter : Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        this.minFilter = minFilter != null ? minFilter : Texture.Filter.MIP_MAP_NEAREST_NEAREST;
        this.uWrap = uWrap != null ? uWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.vWrap = vWrap != null ? vWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropicFilteringLevel = MathUtils.clampInt(anisotropicFilteringLevel,1, GraphicsUtils.getMaxAnisotropicFilterLevel());

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        if (GraphicsUtils.isAnisotropicFilteringSupported()) {
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropicFilteringLevel);
        }
    }

    @Deprecated public Texture(int handle,
                   final int width, final int height,
                   Filter magFilter, Filter minFilter,
                   Wrap uWrap, Wrap vWrap) {
        this.handle = handle;
        this.slot = -1;
        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;
        this.anisotropicFilteringLevel = GraphicsUtils.getMaxAnisotropicFilterLevel();
    }

    protected final void setSlot(final int slot) { this.slot = slot; }
    protected final int  getSlot() { return slot; }

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
        handle = 0;
    }

    public enum Filter {

        NEAREST(GL20.GL_NEAREST),
        LINEAR(GL20.GL_LINEAR),
        MIP_MAP_NEAREST_NEAREST(GL20.GL_NEAREST_MIPMAP_NEAREST),
        MIP_MAP_LINEAR_NEAREST(GL20.GL_LINEAR_MIPMAP_NEAREST),
        MIP_MAP_NEAREST_LINEAR(GL20.GL_NEAREST_MIPMAP_LINEAR),
        MIP_MAP_LINEAR_LINEAR(GL20.GL_LINEAR_MIPMAP_LINEAR)
        ;

        public final int glValue;

        Filter(final int glValue) {
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
