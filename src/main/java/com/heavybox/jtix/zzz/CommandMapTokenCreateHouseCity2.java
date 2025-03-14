package com.heavybox.jtix.zzz;

public class CommandMapTokenCreateHouseCity2 extends Command {

    MapTokenHouseCity2.Direction direction;
    MapTokenHouseCity2.Size size;
    MapTokenHouseCity2.Look look;

    public CommandMapTokenCreateHouseCity2(MapTokenHouseCity2.Direction direction, MapTokenHouseCity2.Size size, MapTokenHouseCity2.Look look) {
        this.direction = direction;
        this.look = look;
        this.size = size;
    }

    @Override
    protected void execute() {
        // TODO: see what's up.
    }

    @Override
    protected void undo() {

    }

}
