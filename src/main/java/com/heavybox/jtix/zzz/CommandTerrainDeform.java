package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class CommandTerrainDeform extends Command {

    public static final String[] regionsGroundLine = {
            "assets/app-ground/ground_line_1.png",
            "assets/app-ground/ground_line_2.png",
            "assets/app-ground/ground_line_3.png",
            "assets/app-ground/ground_line_4.png",
            "assets/app-ground/ground_line_5.png",
            "assets/app-ground/ground_line_6.png",
            "assets/app-ground/ground_line_7.png",
            "assets/app-ground/ground_line_8.png",
            "assets/app-ground/ground_line_9.png",
            "assets/app-ground/ground_line_10.png",
            "assets/app-ground/ground_line_11.png",
            "assets/app-ground/ground_line_12.png",
            "assets/app-ground/ground_line_13.png",
            "assets/app-ground/ground_line_14.png",
            "assets/app-ground/ground_line_15.png",
    };

    public static final String[] regionsGroundBumpLeft = {
            "assets/app-ground/ground_bump_9.png",
            "assets/app-ground/ground_bump_10.png",
            "assets/app-ground/ground_bump_11.png",
            "assets/app-ground/ground_bump_12.png",
            "assets/app-ground/ground_bump_13.png",
    };

    public static final String[] regionsGroundBumpMiddle = {
            "assets/app-ground/ground_bump_1.png",
            "assets/app-ground/ground_bump_2.png",
            "assets/app-ground/ground_bump_3.png",
            "assets/app-ground/ground_bump_4.png",
            "assets/app-ground/ground_bump_5.png",
            "assets/app-ground/ground_bump_6.png",
            "assets/app-ground/ground_bump_7.png",
            "assets/app-ground/ground_bump_8.png",
    };

    public static final String[] regionsGroundBumpRight = regionsGroundBumpLeft;

    protected final GroundType type;
    public final int index;
    public final TextureRegion region;

    public CommandTerrainDeform(TexturePack props, GroundType type, int index) {
        this.type = type;
        this.index = index;

        this.region = switch (type) {
            case LINE -> props.getRegion(regionsGroundLine[index % regionsGroundLine.length]);
            case BUMP_LEFT -> props.getRegion(regionsGroundBumpLeft[index % regionsGroundBumpLeft.length]);
            case BUMP_MIDDLE -> props.getRegion(regionsGroundBumpMiddle[index % regionsGroundBumpMiddle.length]);
            case BUMP_RIGHT -> props.getRegion(regionsGroundBumpRight[index % regionsGroundBumpRight.length]);
        };
    }

    @Override
    protected void execute() {
        // TODO: see what's up.
    }

    @Override
    protected void undo() {

    }

    public enum GroundType {
        LINE,
        BUMP_LEFT,
        BUMP_MIDDLE,
        BUMP_RIGHT,
    }

}
