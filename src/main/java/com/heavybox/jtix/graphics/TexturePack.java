package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;

public class TexturePack implements MemoryResource {

    protected final Texture[]                      textures;
    protected final TexturePacker.Options          options;
    protected final HashMap<String, TextureRegion> namedRegions;

    protected TexturePack(final Texture[] textures, final TexturePacker.Options options, final HashMap<String, TextureRegion> namedRegions) {
        this.textures = textures;
        this.options = options;
        this.namedRegions = namedRegions;
    }

    public TextureRegion getRegion(final String name) {
        final TextureRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException(TexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain a region named " + name);
        return region;
    }

    @Override
    public void delete() {
        // TODO: see how should be implemented.
    }
}
