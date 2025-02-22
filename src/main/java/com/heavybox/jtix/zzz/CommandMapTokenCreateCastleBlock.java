package com.heavybox.jtix.zzz;

public class CommandMapTokenCreateCastleBlock extends Command {

    protected final MapTokenCastleBlock.BlockType type;
    public final int baseIndex;

    public CommandMapTokenCreateCastleBlock(MapTokenCastleBlock.BlockType type, int baseIndex) {
        this.type = type;
        this.baseIndex = baseIndex;
    }

    @Override
    protected void execute() {
        // TODO: see what's up.
    }

    @Override
    protected void undo() {

    }

}
