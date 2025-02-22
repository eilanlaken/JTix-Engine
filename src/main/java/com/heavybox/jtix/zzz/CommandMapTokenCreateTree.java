package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.TextureRegion;

public class CommandMapTokenCreateTree extends Command {

    protected final MapTokenTree.Species species;
    public final int baseIndex;
    public final int trunkIndex;
    public final boolean withFruit;

    public CommandMapTokenCreateTree(MapTokenTree.Species species, int baseIndex, int trunkIndex, boolean withFruit) {
        this.species = species;
        this.baseIndex = baseIndex;
        this.trunkIndex = trunkIndex;
        this.withFruit = withFruit;
    }

    @Override
    protected void execute() {
        // TODO: see what's up.
    }

    @Override
    protected void undo() {

    }

}
