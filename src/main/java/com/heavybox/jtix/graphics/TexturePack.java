package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.io.IOException;
import java.util.HashMap;

public class TexturePack implements MemoryResource {

    protected final Texture[] textures;
    protected final int       extrude;
    protected final int       padding;
    protected final int       maxTextureSize;

    protected final HashMap<String, TextureRegion> namedRegions;

    public TexturePack(Texture[] textures, int extrude, int padding, int maxTextureSize, HashMap<String, TextureRegion> namedRegions) {
        this.textures = textures;
        this.extrude = extrude;
        this.padding = padding;
        this.maxTextureSize = maxTextureSize;
        this.namedRegions = namedRegions;
    }

    public TextureRegion getRegion(final String name) {
        final TextureRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException("The " + TexturePack.class.getSimpleName() + " does not contain a region named " + name);
        return region;
    }

    @Override
    public void delete() {
        // TODO: see how should be implemented.
    }
}
