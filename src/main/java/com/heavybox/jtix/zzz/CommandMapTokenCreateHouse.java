package com.heavybox.jtix.zzz;

public class CommandMapTokenCreateHouse extends Command {

    protected final MapTokenHouse.HouseType type;
    public final int baseIndex;

    public CommandMapTokenCreateHouse(MapTokenHouse.HouseType type, int baseIndex) {
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
