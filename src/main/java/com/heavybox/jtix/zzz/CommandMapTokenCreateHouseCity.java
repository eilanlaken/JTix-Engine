package com.heavybox.jtix.zzz;

public class CommandMapTokenCreateHouseCity extends Command {

    protected final MapTokenHouseCity.HouseType type;
    public final int baseIndex;

    public CommandMapTokenCreateHouseCity(MapTokenHouseCity.HouseType type, int baseIndex) {
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
