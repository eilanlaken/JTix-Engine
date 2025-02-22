package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenTree extends MapToken {

    protected final Species species;
    public final int baseIndex;
    public final int trunkIndex;
    public final boolean withFruit;

    private final TextureRegion base;
    private final TextureRegion trunk;
    private final TextureRegion fruits;

    public MapTokenTree(TexturePack props, Species species, int baseIndex, int trunkIndex, boolean withFruit) {
        super(Type.TREE);
        this.species = species;
        this.baseIndex = baseIndex;
        this.trunkIndex = trunkIndex;
        this.withFruit = withFruit;

        boolean regular = species == Species.REGULAR;
        base = regular ? props.getRegion(ToolBrushTrees.regionsRegular[baseIndex]) : props.getRegion(ToolBrushTrees.regionsCypress[baseIndex]);
        trunk = regular ? props.getRegion(ToolBrushTrees.regionsTrunks[trunkIndex]) : null;
        fruits = withFruit ? regular ? props.getRegion(ToolBrushTrees.regionFruitsRegular) : props.getRegion(ToolBrushTrees.regionFruitsCypress) : null;
    }

    @Override
    public void render(Renderer2D renderer2D) {
        renderer2D.drawTextureRegion(base, x, y, deg, sclX, sclY); // base should never be null.
        if (trunk != null) renderer2D.drawTextureRegion(trunk, x, y, deg, sclX, sclY);
        if (fruits != null) renderer2D.drawTextureRegion(fruits, x, y, deg, sclX, sclY);
    }

    public enum Species {
        REGULAR,
        CYPRESS,
    }

}
