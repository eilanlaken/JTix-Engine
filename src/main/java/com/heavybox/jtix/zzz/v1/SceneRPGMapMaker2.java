package com.heavybox.jtix.zzz.v1;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.zzz.CommandTerrainPaint;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

public class SceneRPGMapMaker2 implements Scene {

    private static final Vector3 screen = new Vector3();
    private final Renderer2D renderer2D = new Renderer2D();

    public boolean inputLeftJustPressed;
    public boolean inputLeftJustRelease;
    public boolean inputLeftPressedAndMoved;

    /* assets */
    private TexturePack props;
    private Texture terrainGrass, terrainWater;
    private Texture terrainFieldBase, terrainFieldLines, terrainFieldFlowers;

    // tools
    private Tool activeTool = null;
    private ToolTerrainCarve toolTerrainCarve;

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);
    public Array<Command> commandHistory = new Array<>(true, 10);
    private final Array<CommandTerrainCarve> commandTerrainCarves = new Array<>(true, 100);
    public final Array<MapToken> mapTokens = new Array<>(true, 10);

    @Override
    public void setup() {
        try {
            ToolsTexturePacker.packTextures("assets/app-texture-packs-2", "medieval-pack", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192,
                    "assets/app-trees/flower_1.png",
                    "assets/app-trees/tree_cypress_1.png",
                    "assets/app-trees/tree_cypress_2.png",
                    "assets/app-trees/tree_cypress_3.png",
                    "assets/app-trees/tree_cypress_4.png",
                    "assets/app-trees/tree_regular_1.png",
                    "assets/app-trees/tree_regular_2.png",
                    "assets/app-trees/tree_regular_3.png",
                    "assets/app-trees/tree_regular_4.png",
                    "assets/app-trees/tree_regular_5.png",
                    "assets/app-trees/tree_regular_6.png",
                    "assets/app-trees/tree_regular_trunk_1.png",
                    "assets/app-trees/tree_regular_trunk_2.png",
                    "assets/app-trees/tree_regular_trunk_3.png",
                    "assets/app-trees/tree_regular_trunk_4.png",
                    "assets/app-trees/tree_regular_trunk_5.png",
                    "assets/app-trees/tree_regular_trunk_6.png",
                    "assets/app-trees/tree_regular_trunk_7.png",
                    "assets/app-trees/tree_regular_trunk_8.png",
                    "assets/app-trees/tree_regular_trunk_9.png",
                    "assets/app-trees/tree_regular_trunk_10.png",
                    "assets/app-trees/tree_regular_fruits.png",
                    "assets/app-trees/tree_cypress_fruits.png",
                    "assets/app-trees/bush_1.png",
                    "assets/app-trees/bush_2.png",
                    "assets/app-trees/bush_3.png",
                    "assets/app-trees/bush_4.png",
                    "assets/app-trees/bush_5.png",
                    "assets/app-trees/bush_6.png",

                    "assets/app-clouds/clouds_1.png",
                    "assets/app-clouds/clouds_2.png",
                    "assets/app-clouds/clouds_3.png",
                    "assets/app-clouds/clouds_4.png",
                    "assets/app-clouds/clouds_5.png",
                    "assets/app-clouds/clouds_6.png",

                    // village houses
                    "assets/app-village-houses/house-diagonal-big-base.png",
                    "assets/app-village-houses/house-diagonal-big-overlay_0.png",
                    "assets/app-village-houses/house-diagonal-big-overlay_1.png",
                    "assets/app-village-houses/house-diagonal-big-overlay_2.png",
                    "assets/app-village-houses/house-diagonal-big-overlay_3.png",
                    "assets/app-village-houses/house-diagonal-big-overlay_4.png",
                    "assets/app-village-houses/house-diagonal-small-base.png",
                    "assets/app-village-houses/house-diagonal-small-overlay_0.png",
                    "assets/app-village-houses/house-diagonal-small-overlay_1.png",
                    "assets/app-village-houses/house-diagonal-small-overlay_2.png",
                    "assets/app-village-houses/house-diagonal-small-overlay_3.png",
                    "assets/app-village-houses/house-diagonal-small-overlay_4.png",
                    "assets/app-village-houses/house-horizontal-big-base.png",
                    "assets/app-village-houses/house-horizontal-big-overlay_0.png",
                    "assets/app-village-houses/house-horizontal-big-overlay_1.png",
                    "assets/app-village-houses/house-horizontal-big-overlay_2.png",
                    "assets/app-village-houses/house-horizontal-big-overlay_3.png",
                    "assets/app-village-houses/house-horizontal-big-overlay_4.png",
                    "assets/app-village-houses/house-horizontal-small-base.png",
                    "assets/app-village-houses/house-horizontal-small-overlay_0.png",
                    "assets/app-village-houses/house-horizontal-small-overlay_1.png",
                    "assets/app-village-houses/house-horizontal-small-overlay_2.png",
                    "assets/app-village-houses/house-horizontal-small-overlay_3.png",
                    "assets/app-village-houses/house-horizontal-small-overlay_4.png",
                    "assets/app-village-houses/house-vertical-big-base.png",
                    "assets/app-village-houses/house-vertical-big-overlay_0.png",
                    "assets/app-village-houses/house-vertical-big-overlay_1.png",
                    "assets/app-village-houses/house-vertical-big-overlay_2.png",
                    "assets/app-village-houses/house-vertical-big-overlay_3.png",
                    "assets/app-village-houses/house-vertical-big-overlay_4.png",
                    "assets/app-village-houses/house-vertical-small-base.png",
                    "assets/app-village-houses/house-vertical-small-overlay_0.png",
                    "assets/app-village-houses/house-vertical-small-overlay_1.png",
                    "assets/app-village-houses/house-vertical-small-overlay_2.png",
                    "assets/app-village-houses/house-vertical-small-overlay_3.png",
                    "assets/app-village-houses/house-vertical-small-overlay_4.png",
                    "assets/app-village-houses/house-vertical-tiny-base.png",
                    "assets/app-village-houses/house-vertical-tiny-overlay_0.png",
                    "assets/app-village-houses/house-vertical-tiny-overlay_1.png",
                    "assets/app-village-houses/house-vertical-tiny-overlay_2.png",
                    "assets/app-village-houses/house-vertical-tiny-overlay_3.png",
                    "assets/app-village-houses/house-vertical-tiny-overlay_4.png",

                    // props
                    "assets/app-props/barrels_0.png",
                    "assets/app-props/barrels_1.png",
                    "assets/app-props/barrels_2.png",
                    "assets/app-props/barrels_3.png",
                    "assets/app-props/barrels_4.png",
                    "assets/app-props/boxes_0.png",
                    "assets/app-props/boxes_1.png",
                    "assets/app-props/boxes_2.png",
                    "assets/app-props/boxes_3.png",
                    "assets/app-props/bridge-part.png",
                    "assets/app-props/chopped-trunk.png",
                    "assets/app-props/fence-bar.png",
                    "assets/app-props/fence-post.png",
                    "assets/app-props/lodge_0.png",
                    "assets/app-props/lodge_1.png",
                    "assets/app-props/lodge_2.png",
                    "assets/app-props/pile-big.png",
                    "assets/app-props/pile-small.png",
                    "assets/app-props/sack.png",
                    "assets/app-props/scarecrow_0.png",
                    "assets/app-props/scarecrow_1.png",
                    "assets/app-props/signs.png",
                    "assets/app-props/straw_0.png",
                    "assets/app-props/straw_1.png",
                    "assets/app-props/tower_0.png",
                    "assets/app-props/tower_1.png",
                    "assets/app-props/windmill-base_0.png",
                    "assets/app-props/windmill-base_1.png",
                    "assets/app-props/windmill-base_2.png",
                    "assets/app-props/windmill-base_3.png",
                    "assets/app-props/windmill-mill_0.png",
                    "assets/app-props/windmill-mill_1.png",

                    "assets/app-city-houses/house-diagonal-big-foundation.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_0.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_1.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_2.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_3.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_4.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_5.png",
                    "assets/app-city-houses/house-diagonal-big-foundation-overlay_6.png",
                    "assets/app-city-houses/house-diagonal-big-roof-base_blue.png",
                    "assets/app-city-houses/house-diagonal-big-roof-base_grey.png",
                    "assets/app-city-houses/house-diagonal-big-roof-base_purple.png",
                    "assets/app-city-houses/house-diagonal-big-roof-base_red.png",
                    "assets/app-city-houses/house-diagonal-big-roof-base_yellow.png",
                    "assets/app-city-houses/house-diagonal-big-roof-overlay_0.png",
                    "assets/app-city-houses/house-diagonal-big-roof-overlay_1.png",
                    "assets/app-city-houses/house-diagonal-big-roof-overlay_2.png",
                    "assets/app-city-houses/house-diagonal-big-roof-overlay_3.png",
                    "assets/app-city-houses/house-diagonal-big-roof-overlay_4.png",
                    "assets/app-city-houses/house-diagonal-small-foundation.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_0.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_1.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_2.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_3.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_4.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_5.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_6.png",
                    "assets/app-city-houses/house-diagonal-small-foundation-overlay_7.png",
                    "assets/app-city-houses/house-diagonal-small-roof-base_blue.png",
                    "assets/app-city-houses/house-diagonal-small-roof-base_grey.png",
                    "assets/app-city-houses/house-diagonal-small-roof-base_purple.png",
                    "assets/app-city-houses/house-diagonal-small-roof-base_red.png",
                    "assets/app-city-houses/house-diagonal-small-roof-base_yellow.png",
                    "assets/app-city-houses/house-diagonal-small-roof-overlay_0.png",
                    "assets/app-city-houses/house-diagonal-small-roof-overlay_1.png",
                    "assets/app-city-houses/house-diagonal-small-roof-overlay_2.png",
                    "assets/app-city-houses/house-diagonal-small-roof-overlay_3.png",
                    "assets/app-city-houses/house-diagonal-small-roof-overlay_4.png",
                    "assets/app-city-houses/house-horizontal-big-foundation.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_0.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_1.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_2.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_3.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_4.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_5.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_6.png",
                    "assets/app-city-houses/house-horizontal-big-foundation-overlay_7.png",
                    "assets/app-city-houses/house-horizontal-big-roof-base_blue.png",
                    "assets/app-city-houses/house-horizontal-big-roof-base_purple.png",
                    "assets/app-city-houses/house-horizontal-big-roof-base_grey.png",
                    "assets/app-city-houses/house-horizontal-big-roof-base_red.png",
                    "assets/app-city-houses/house-horizontal-big-roof-base_yellow.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_0.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_1.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_2.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_3.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_4.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_5.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_6.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_7.png",
                    "assets/app-city-houses/house-horizontal-big-roof-overlay_8.png",
                    "assets/app-city-houses/house-horizontal-small-foundation.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_0.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_1.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_2.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_3.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_4.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_5.png",
                    "assets/app-city-houses/house-horizontal-small-foundation-overlay_6.png",
                    "assets/app-city-houses/house-horizontal-small-roof-base_blue.png",
                    "assets/app-city-houses/house-horizontal-small-roof-base_grey.png",
                    "assets/app-city-houses/house-horizontal-small-roof-base_purple.png",
                    "assets/app-city-houses/house-horizontal-small-roof-base_red.png",
                    "assets/app-city-houses/house-horizontal-small-roof-base_yellow.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_0.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_1.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_2.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_3.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_4.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_5.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_6.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_7.png",
                    "assets/app-city-houses/house-horizontal-small-roof-overlay_8.png",
                    "assets/app-city-houses/house-vertical-big-foundation.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_0.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_1.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_2.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_3.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_4.png",
                    "assets/app-city-houses/house-vertical-big-foundation-overlay_5.png",
                    "assets/app-city-houses/house-vertical-big-roof-base_blue.png",
                    "assets/app-city-houses/house-vertical-big-roof-base_grey.png",
                    "assets/app-city-houses/house-vertical-big-roof-base_purple.png",
                    "assets/app-city-houses/house-vertical-big-roof-base_red.png",
                    "assets/app-city-houses/house-vertical-big-roof-base_yellow.png",
                    "assets/app-city-houses/house-vertical-big-roof-overlay_0.png",
                    "assets/app-city-houses/house-vertical-big-roof-overlay_1.png",
                    "assets/app-city-houses/house-vertical-big-roof-overlay_2.png",
                    "assets/app-city-houses/house-vertical-big-roof-overlay_3.png",
                    "assets/app-city-houses/house-vertical-big-roof-overlay_4.png",
                    "assets/app-city-houses/house-vertical-small-foundation.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_0.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_1.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_2.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_3.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_4.png",
                    "assets/app-city-houses/house-vertical-small-foundation-overlay_5.png",
                    "assets/app-city-houses/house-vertical-small-roof-base_blue.png",
                    "assets/app-city-houses/house-vertical-small-roof-base_grey.png",
                    "assets/app-city-houses/house-vertical-small-roof-base_purple.png",
                    "assets/app-city-houses/house-vertical-small-roof-base_red.png",
                    "assets/app-city-houses/house-vertical-small-roof-base_yellow.png",
                    "assets/app-city-houses/house-vertical-small-roof-overlay_0.png",
                    "assets/app-city-houses/house-vertical-small-roof-overlay_1.png",
                    "assets/app-city-houses/house-vertical-small-roof-overlay_2.png",
                    "assets/app-city-houses/house-vertical-small-roof-overlay_3.png",
                    "assets/app-city-houses/house-vertical-small-roof-overlay_4.png",

                    "assets/app-castles/castle-building-block_1.png",
                    "assets/app-castles/castle-building-block_2.png",
                    "assets/app-castles/castle-building-block_3.png",
                    "assets/app-castles/castle-building-block_4.png",
                    "assets/app-castles/castle-building-block_5.png",
                    "assets/app-castles/castle-building-block_6.png",
                    "assets/app-castles/castle-building-block_7.png",
                    "assets/app-castles/castle-building-block_8.png",
                    "assets/app-castles/castle-building-block_9.png",
                    "assets/app-castles/castle-building-block_10.png",
                    "assets/app-castles/castle-building-block_11.png",
                    "assets/app-castles/castle-building-block_12.png",
                    "assets/app-castles/castle-building-block_13.png",
                    "assets/app-castles/castle-building-block_14.png",
                    "assets/app-castles/castle-building-block_15.png",
                    "assets/app-castles/castle-building-block_16.png",
                    "assets/app-castles/castle-building-block_17.png",
                    "assets/app-castles/castle-building-block_18.png",
                    "assets/app-castles/castle-building-block_19.png",
                    "assets/app-castles/castle-building-block_20.png",
                    "assets/app-castles/castle-building-block_21.png",
                    "assets/app-castles/castle-building-block_22.png",
                    "assets/app-castles/castle-building-block_23.png",
                    "assets/app-castles/castle-building-block_24.png",
                    "assets/app-castles/castle-building-block_25.png",
                    "assets/app-castles/castle-building-block_26.png",
                    "assets/app-castles/castle-building-block_27.png",
                    "assets/app-castles/castle-building-block_28.png",
                    "assets/app-castles/castle-building-block_29.png",
                    "assets/app-castles/castle-building-block_30.png",
                    "assets/app-castles/castle-building-block_31.png",
                    "assets/app-castles/castle-building-block_32.png",
                    "assets/app-castles/castle-building-block_33.png",
                    "assets/app-castles/castle-tower-block_1.png",
                    "assets/app-castles/castle-tower-block_2.png",
                    "assets/app-castles/castle-tower-block_3.png",
                    "assets/app-castles/castle-tower-block_4.png",
                    "assets/app-castles/castle-tower-block_5.png",
                    "assets/app-castles/castle-tower-block_6.png",
                    "assets/app-castles/castle-tower-block_7.png",
                    "assets/app-castles/castle-tower-block_8.png",
                    "assets/app-castles/castle-tower-block_9.png",
                    "assets/app-castles/castle-tower-block_10.png",
                    "assets/app-castles/castle-tower-block_11.png",
                    "assets/app-castles/castle-tower-block_12.png",
                    "assets/app-castles/castle-tower-block_13.png",
                    "assets/app-castles/castle-tower-block_14.png",
                    "assets/app-castles/castle-tower-block_15.png",
                    "assets/app-castles/castle-tower-block_16.png",
                    "assets/app-castles/castle-tower-block_17.png",
                    "assets/app-castles/castle-tower-block_18.png",
                    "assets/app-castles/castle-tower-block_19.png",
                    "assets/app-castles/castle-tower-block_20.png",
                    "assets/app-castles/castle-tower-block_21.png",
                    "assets/app-castles/castle-wall-back-block_1.png",
                    "assets/app-castles/castle-wall-back-block_2.png",
                    "assets/app-castles/castle-wall-back-block_3.png",
                    "assets/app-castles/castle-wall-back-block_4.png",
                    "assets/app-castles/castle-wall-back-block_5.png",
                    "assets/app-castles/castle-wall-back-block_6.png",
                    "assets/app-castles/castle-wall-back-block_7.png",
                    "assets/app-castles/castle-wall-back-block_8.png",
                    "assets/app-castles/castle-wall-front-block_1.png",
                    "assets/app-castles/castle-wall-front-block_2.png",
                    "assets/app-castles/castle-wall-front-block_3.png",
                    "assets/app-castles/castle-wall-front-block_4.png",
                    "assets/app-castles/castle-wall-front-block_5.png",
                    "assets/app-castles/castle-wall-front-block_6.png",
                    "assets/app-castles/castle-wall-front-block_7.png",
                    "assets/app-castles/castle-wall-front-block_8.png",
                    "assets/app-castles/castle-wall-front-block_9.png",
                    "assets/app-castles/castle-wall-front-block_10.png",

                    // terrain
                    "assets/app-terrain-2/grass-lines_0.png",
                    "assets/app-terrain-2/grass-lines_1.png",
                    "assets/app-terrain-2/grass-lines_2.png",
                    "assets/app-terrain-2/grass-lines_3.png",
                    "assets/app-terrain-2/grass-lines_4.png",
                    "assets/app-terrain-2/hills_0.png",
                    "assets/app-terrain-2/hills_1.png",
                    "assets/app-terrain-2/hills_2.png",
                    "assets/app-terrain-2/hills_3.png",
                    "assets/app-terrain-2/hills_4.png",
                    "assets/app-terrain-2/hills_5.png",
                    "assets/app-terrain-2/marks_0.png",
                    "assets/app-terrain-2/marks_1.png",
                    "assets/app-terrain-2/marks_2.png",
                    "assets/app-terrain-2/marks_3.png",
                    "assets/app-terrain-2/mountain-brown_0.png",
                    "assets/app-terrain-2/mountain-brown_1.png",
                    "assets/app-terrain-2/mountain-brown_2.png",
                    "assets/app-terrain-2/mountain-brown_3.png",
                    "assets/app-terrain-2/mountain-brown_4.png",
                    "assets/app-terrain-2/mountain-brown_5.png",
                    "assets/app-terrain-2/mountain-brown_6.png",
                    "assets/app-terrain-2/mountain-brown_7.png",
                    "assets/app-terrain-2/mountain-green_0.png",
                    "assets/app-terrain-2/mountain-green_1.png",
                    "assets/app-terrain-2/mountain-green_2.png",
                    "assets/app-terrain-2/mountain-green_3.png",
                    "assets/app-terrain-2/mountain-green_4.png",
                    "assets/app-terrain-2/mountain-green_5.png",
                    "assets/app-terrain-2/mountain-green_6.png",
                    "assets/app-terrain-2/mountain-green_7.png",
                    "assets/app-terrain-2/mountain-grey_0.png",
                    "assets/app-terrain-2/mountain-grey_1.png",
                    "assets/app-terrain-2/mountain-grey_2.png",
                    "assets/app-terrain-2/mountain-grey_3.png",
                    "assets/app-terrain-2/mountain-grey_4.png",
                    "assets/app-terrain-2/mountain-grey_5.png",
                    "assets/app-terrain-2/mountain-grey_6.png",
                    "assets/app-terrain-2/mountain-grey_7.png",
                    "assets/app-terrain-2/mountain-olive_0.png",
                    "assets/app-terrain-2/mountain-olive_1.png",
                    "assets/app-terrain-2/mountain-olive_2.png",
                    "assets/app-terrain-2/mountain-olive_3.png",
                    "assets/app-terrain-2/mountain-olive_4.png",
                    "assets/app-terrain-2/mountain-olive_5.png",
                    "assets/app-terrain-2/mountain-olive_6.png",
                    "assets/app-terrain-2/mountain-olive_7.png",
                    "assets/app-terrain-2/rock-big_0.png",
                    "assets/app-terrain-2/rock-big_1.png",
                    "assets/app-terrain-2/rock-big_2.png",
                    "assets/app-terrain-2/rock-big_3.png",
                    "assets/app-terrain-2/rock-big_4.png",
                    "assets/app-terrain-2/rock-big_5.png",
                    "assets/app-terrain-2/rock-big_6.png",
                    "assets/app-terrain-2/rock-big_7.png",
                    "assets/app-terrain-2/rock-big_8.png",
                    "assets/app-terrain-2/rock-big_9.png",
                    "assets/app-terrain-2/rock-small_0.png",
                    "assets/app-terrain-2/rock-small_1.png",
                    "assets/app-terrain-2/rock-small_2.png",
                    "assets/app-terrain-2/rock-small_3.png",
                    "assets/app-terrain-2/rock-small_4.png",
                    "assets/app-terrain-2/rock-small_5.png",
                    "assets/app-terrain-2/horizontal-lines_0.png",
                    "assets/app-terrain-2/horizontal-lines_1.png",
                    "assets/app-terrain-2/horizontal-lines_2.png",
                    "assets/app-terrain-2/horizontal-lines_3.png"
            );
        } catch (Exception e) {
            e.printStackTrace();
        } // PACK MEDIEVAL MAP PROPS

        // TODO: make the program CRASH and not thread-locked when file can't load.
        //Assets.loadTexture("assets/app-terrain/grass-1024.png");
        Assets.loadTexture("assets/app-terrain-2/terrain-grass_1920x1080.png");
        Assets.loadTexture("assets/app-terrain-2/terrain-water_1920x1080.png");
        Assets.loadTexture("assets/app-brushes/terrain-brush_0.png");

        Assets.loadTexture("assets/app-terrain/wheat-field-base.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.loadTexture("assets/app-terrain/wheat-field-lines.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);

        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        //Assets.loadTexturePack("assets/app-texture-packs/icons.yml");
        //Assets.loadTexturePack("assets/app-texture-packs/medieval-pack.yml");
        Assets.loadShader("outline", "assets/app-shaders/graphics-2d-shader-texture-outline.vert", "assets/app-shaders/graphics-2d-shader-texture-outline.frag");

        Assets.finishLoading();

        //props = Assets.get("assets/app-texture-packs/medieval-pack.yml");

        //terrainWater = Assets.get("assets/app-terrain/type-water-base-1024.png");
        terrainGrass = Assets.get("assets/app-terrain-2/terrain-grass_1920x1080.png");
        terrainWater = Assets.get("assets/app-terrain-2/terrain-water_1920x1080.png");

        terrainFieldBase = Assets.get("assets/app-terrain/wheat-field-base.png");
        terrainFieldLines = Assets.get("assets/app-terrain/wheat-field-lines.png");

    }

    @Override
    public void start() {
        toolTerrainCarve = new ToolTerrainCarve(this);
        selectTool(toolTerrainCarve);
    }

    @Override
    public void update() {
        step();

        // get all terrain draw commands history
        commandTerrainCarves.clear();
        for (Command command : commandHistory) { // TODO: iterate until last index.
            if (command instanceof CommandTerrainCarve) commandTerrainCarves.add((CommandTerrainCarve) command);
        }

        // render scene
        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);

        // draw terrain
        // create terrain stencil mask
        renderer2D.beginStencil();
        renderer2D.stencilBufferClear(CommandTerrainPaint.GRASS_MASK);
        for (CommandTerrainCarve command : commandTerrainCarves) {
            renderer2D.setStencilModeReplace(command.mask);
            renderer2D.drawCircleFilled(30, 5, command.x, command.y, command.deg, command.sclX, command.sclY);
        }
        renderer2D.endStencil();

        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.WATER_MASK);
        renderer2D.drawTexture(terrainWater, 0, 0, 0, 1, 1);

        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.GRASS_MASK);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, 1);
        // draw wheat fields here. they are masked by the ground.

        renderer2D.disableMasking();
        renderer2D.setColor(Color.WHITE);
        mapTokens.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (MapToken token : mapTokens) {
            token.render(renderer2D);
        }

        // render tool overlay here
        //if (toolBrushTrees.active) toolBrushTrees.renderToolOverlay(renderer2D, screen.x, screen.y, 0, 1,1);
        if (activeTool != null) activeTool.renderToolOverlay(renderer2D, screen.x, screen.y, 0, 1,1);


        renderer2D.end();


        // render UI
        renderer2D.begin();
        renderer2D.end();

    }

    private void step() {
        if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && !Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.position.x -= 1.5f * Input.mouse.getXDelta();
            camera.position.y += 1.5f * Input.mouse.getYDelta();
            // TODO: set zoom limits
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.zoom += Input.mouse.getYDelta() * 0.05f;
        }
        screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        inputLeftJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        inputLeftJustRelease = Input.mouse.isButtonJustReleased(Mouse.Button.LEFT);
        inputLeftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Math.abs(Input.mouse.getXDelta()) > 0 || Math.abs(Input.mouse.getYDelta()) > 0);

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            selectTool(toolTerrainCarve);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_4)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_5)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_6)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_7)) {

        }

        activeTool.frameUpdate();
    }

    private void selectTool(Tool tool) {
        if (tool == null) {
            activeTool.deselect();
            activeTool = null;
            //Graphics.setCursorDefault(); // TODO
        } else if (activeTool == null) {
            activeTool = tool;
            activeTool.select();
            //Graphics.setCursorNone();
        } else if (activeTool != tool) { // already selected, do nothing
            activeTool.deselect();
            activeTool = tool;
            activeTool.select();
            //Graphics.setCursorNone();
        }
    }

    public void save() {

    }

    public void export() {

    }

    public void undo() {

    }

    public void redo() {

    }

    @Override
    public void finish() {

    }


    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }

    @Override
    public void windowResized(int width, int height) {
        camera.viewportWidth = Graphics.getWindowWidth();
        camera.viewportHeight = Graphics.getWindowHeight();

        // TODO: resize frame buffer
    }

}
