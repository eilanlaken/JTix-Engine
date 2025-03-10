package com.heavybox.jtix.zzz;

public class CommandMapTokenCreateHouseVillage extends Command {

    protected final MapTokenHouseVillage.HouseType type;
    public final int baseIndex;

    public CommandMapTokenCreateHouseVillage(MapTokenHouseVillage.HouseType type, int baseIndex) {
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
