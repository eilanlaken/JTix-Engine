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
            "assets/app-ground/ground_line_16.png",
            "assets/app-ground/ground_line_17.png",
            "assets/app-ground/ground_line_18.png",
            "assets/app-ground/ground_line_19.png",
            "assets/app-ground/ground_line_20.png",
            "assets/app-ground/ground_line_21.png",
            "assets/app-ground/ground_line_22.png",
            "assets/app-ground/ground_line_23.png",
            "assets/app-ground/ground_line_24.png",
            "assets/app-ground/ground_line_25.png"
    };

    public static final String[] regionsGroundBumpLeft = {
            "assets/app-terrain/bump_9.png",
            "assets/app-terrain/bump_10.png",
            "assets/app-terrain/bump_11.png",
            "assets/app-terrain/bump_12.png",
            "assets/app-terrain/bump_13.png",
    };

    public static final String[] regionsGroundBumpMiddle = {
            "assets/app-terrain/bump_1.png",
            "assets/app-terrain/bump_2.png",
            "assets/app-terrain/bump_3.png",
            "assets/app-terrain/bump_4.png",
            "assets/app-terrain/bump_5.png",
            "assets/app-terrain/bump_6.png",
            "assets/app-terrain/bump_7.png",
            "assets/app-terrain/bump_8.png",
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
        ;

        public boolean isRight() {
            return this == BUMP_RIGHT;
        }

        public static TextureRegion getRegion(TexturePack props, GroundType type, int index) {
            return switch (type) {
                case LINE -> props.getRegion(regionsGroundLine[index % regionsGroundLine.length]);
                case BUMP_LEFT -> props.getRegion(regionsGroundBumpLeft[index % regionsGroundBumpLeft.length]);
                case BUMP_MIDDLE -> props.getRegion(regionsGroundBumpMiddle[index % regionsGroundBumpMiddle.length]);
                case BUMP_RIGHT -> props.getRegion(regionsGroundBumpRight[index % regionsGroundBumpRight.length]);
            };
        }

    }

}
