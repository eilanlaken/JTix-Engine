package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TexturePack implements MemoryResource {

    final Texture[]               textures;
    final HashMap<String, Region> namedRegions;

    @SuppressWarnings("unchecked")
    public TexturePack(Texture[] textures, String yaml) {
        this.textures = textures;
        this.namedRegions = new HashMap<>();
        try {
            Map<String, Object> data = AssetUtils.yaml.load(yaml);
            List<Map<String, Object>> regions = (List<Map<String, Object>>) data.get("regions");
            for (Map<String, Object> regionData : regions) {
                String name = (String) regionData.get("name");
                Texture texture = textures[(int) regionData.get("textureIndex")];
                int offsetX = (int) regionData.get("offsetX");
                int offsetY = (int) regionData.get("offsetY");
                int originalWidth = (int) regionData.get("originalWidth");
                int originalHeight = (int) regionData.get("originalHeight");
                int packedWidth = (int) regionData.get("packedWidth");
                int packedHeight = (int) regionData.get("packedHeight");
                int x = (int) regionData.get("x");
                int y = (int) regionData.get("y");
                Region region = new Region(texture, x, y, offsetX, offsetY, packedWidth, packedHeight, originalWidth, originalHeight);
                namedRegions.put(name, region);
            }
        } catch (YAMLException e) {
            throw new GraphicsException("Failed to create " + TexturePack.class.getSimpleName() + " from invalid yaml: " + yaml);
        } catch (Exception e) {
            throw new GraphicsException("Failed to create " + TexturePack.class.getSimpleName());
        }
    }

    public Region getRegion(final String name) {
        final Region region = namedRegions.get(name);
        if (region == null) throw new RuntimeException("The " + TexturePack.class.getSimpleName() + " does not contain a region named " + name);
        return region;
    }

    @Override
    public void delete() {
        // TODO: see how should be implemented.
    }

    public class Region {

        public final Texture texture;

        public final float x;
        public final float y;
        public final float offsetX;
        public final float offsetY;
        public final float packedWidth;
        public final float packedHeight;
        public final float originalWidth;
        public final float originalHeight;
        public final float packedWidthHalf;
        public final float packedHeightHalf;
        public final float originalWidthHalf;
        public final float originalHeightHalf;
        public final float u1;
        public final float v1;
        public final float u2;
        public final float v2;

        Region(final Texture texture,
               int x, int y, int offsetX, int offsetY,
               int packedWidth, int packedHeight, int originalWidth, int originalHeight) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.packedWidth = packedWidth;
            this.packedHeight = packedHeight;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.packedWidthHalf = packedWidth * 0.5f;
            this.packedHeightHalf = packedHeight * 0.5f;
            this.originalWidthHalf = originalWidth * 0.5f;
            this.originalHeightHalf = originalHeight * 0.5f;
            float invTexWidth = 1.0f / (float) this.texture.width;
            float invTexHeight = 1.0f / (float) this.texture.height;
            float u1 = (float)x * invTexWidth;
            float v1 = (float)y * invTexHeight;
            float u2 = (float)(x + packedWidth) * invTexWidth;
            float v2 = (float)(y + packedHeight) * invTexHeight;
            if (this.packedWidth == 1 && this.packedHeight == 1) {
                float adjustX = 0.25f / (float) texture.width;
                u1 += adjustX;
                u2 -= adjustX;
                float adjustY = 0.25f / (float) texture.height;
                v1 += adjustY;
                v2 -= adjustY;
            }
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }

    }

}
