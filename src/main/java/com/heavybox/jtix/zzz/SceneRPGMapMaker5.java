package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.widgets_4.NodeContainer;
import com.heavybox.jtix.widgets_4.NodeContainerHorizontal;
import com.heavybox.jtix.widgets_4.Widget;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

public class SceneRPGMapMaker5 implements Scene {

    private static final Vector3 screen = new Vector3();
    private final Renderer2D renderer2D = new Renderer2D();
    /* ui */
    private final Widget toolbarWidget = new Widget();
    private final Widget menuBarWidget = new Widget();

    /* assets */
    private TexturePack icons;
    private TexturePack props;
    private Texture terrainWater;
    private Texture terrainGrass;
    private Texture terrainRoad;
    private Texture terrainWheatBase;
    private Texture terrainWheatLines;

    // tools
    private Tool activeTool = null;
    private final ToolTerrainPaint toolTerrainPaint = new ToolTerrainPaint();
    private final ToolBrushTrees toolBrushTrees = new ToolBrushTrees();
    private ToolCastleGenerator toolCastleGenerator;
    private ToolVillageGenerator toolVillageGenerator;
    private ToolCityBlock toolCityBlock;
    private ToolTerrainDeform toolTerrainDeform;
    private ToolWheatField toolWheatField;

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);
    public Array<Command> commandHistory = new Array<>(true, 10);
    public int commandChainIndex = -1;
    private final Array<CommandTerrainPaint> commandsTerrainPaint = new Array<>(true, 100);
    private final Array<CommandTerrainDeform> commandsTerrainDeform = new Array<>(true, 100);
    private final Array<CommandTerrainDrawWheat> commandsDrawWheat = new Array<>(true, 5);
    public final Array<MapToken> mapTokens = new Array<>(true, 10);
    // TODO: make a copy array of map tokens for clearing(), copying(), sorting() then rendering().

    @Override
    public void setup() {
        try {
            ToolsTexturePacker.packTextures("assets/app-texture-packs", "icons", 2, 2, ToolsTexturePacker.TexturePackSize.LARGE_2048,
                    // toolbar
                    "assets/app-icons/brush.png",
                    "assets/app-icons/export.png",
                    "assets/app-icons/path.png",
                    "assets/app-icons/select.png",
                    "assets/app-icons/terrain.png",
                    "assets/app-icons/text.png",
                    "assets/app-icons/move.png",
                    // menu-bar
                    "assets/app-icons/new.png",
                    "assets/app-icons/open.png",
                    "assets/app-icons/blank.png",
                    "assets/app-icons/save.png",
                    "assets/app-icons/exit.png"
            );
        } catch (Exception ignored) {} // PACK ICONS
        try {
            ToolsTexturePacker.packTextures("assets/app-texture-packs", "medieval-pack", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192,
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

                    "assets/app-village/house_0.png",
                    "assets/app-village/house_1.png",
                    "assets/app-village/house_2.png",
                    "assets/app-village/house_3.png",
                    "assets/app-village/house_4.png",
                    "assets/app-village/house_5.png",
                    "assets/app-village/house_6.png",
                    "assets/app-village/house_7.png",
                    "assets/app-village/house_8.png",
                    "assets/app-village/house_9.png",
                    "assets/app-village/house_10.png",
                    "assets/app-village/house_11.png",
                    "assets/app-village/house_12.png",
                    "assets/app-village/house_13.png",
                    "assets/app-village/house_14.png",
                    "assets/app-village/house_15.png",
                    "assets/app-village/house_16.png",
                    "assets/app-village/house_17.png",
                    "assets/app-village/house_18.png",
                    "assets/app-village/house_19.png",
                    "assets/app-village/house_20.png",
                    "assets/app-village/house_21.png",
                    "assets/app-village/house_22.png",
                    "assets/app-village/house_23.png",
                    "assets/app-village/house_24.png",
                    "assets/app-village/house_25.png",
                    "assets/app-village/house_26.png",
                    "assets/app-village/house_27.png",
                    "assets/app-village/house_28.png",
                    "assets/app-village/house_29.png",
                    "assets/app-village/house_30.png",
                    "assets/app-village/house_31.png",
                    "assets/app-village/house_32.png",
                    "assets/app-village/house_33.png",
                    "assets/app-village/house_34.png",
                    "assets/app-village/house_35.png",
                    "assets/app-village/house_36.png",
                    "assets/app-village/house_37.png",
                    "assets/app-village/house_38.png",
                    "assets/app-village/house_39.png",
                    "assets/app-village/house_40.png",
                    "assets/app-village/house_41.png",
                    "assets/app-village/house_42.png",
                    "assets/app-village/house_43.png",
                    "assets/app-village/house_44.png", // windmill 1
                    "assets/app-village/house_45.png", // windmill 2
                    "assets/app-village/prop_fence_bar.png",
                    "assets/app-village/prop_fence_post.png",
                    "assets/app-village/prop_log_1.png",
                    "assets/app-village/prop_log_2.png",
                    "assets/app-village/prop_log_3.png",
                    "assets/app-village/prop_pile.png",
                    "assets/app-village/prop_tower_1.png",
                    "assets/app-village/prop_tower_2.png",
                    "assets/app-village/prop_sack_of_flour.png",
                    "assets/app-village/prop_scarecrow.png",
                    "assets/app-village/prop_straw_1.png",
                    "assets/app-village/prop_straw_2.png",
                    "assets/app-village/prop_trunk_chopped.png",

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

                    "assets/app-terrain/bump_1.png",
                    "assets/app-terrain/bump_2.png",
                    "assets/app-terrain/bump_3.png",
                    "assets/app-terrain/bump_4.png",
                    "assets/app-terrain/bump_5.png",
                    "assets/app-terrain/bump_6.png",
                    "assets/app-terrain/bump_7.png",
                    "assets/app-terrain/bump_8.png",
                    "assets/app-terrain/bump_9.png",
                    "assets/app-terrain/bump_10.png",
                    "assets/app-terrain/bump_11.png",
                    "assets/app-terrain/bump_12.png",
                    "assets/app-terrain/bump_13.png",

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
            );
        } catch (Exception e) {
            e.printStackTrace();
        } // PACK MEDIEVAL MAP PROPS

        // TODO: make the program CRASH and not thread-locked when file can't load.
        //Assets.loadTexture("assets/app-terrain/grass-1024.png");
        Assets.loadTexture("assets/app-terrain/grass-new-1024.png");
        Assets.loadTexture("assets/app-terrain/water-1024.png");
        //Assets.loadTexture("assets/app-terrain/water-new-1024.png");
        //Assets.loadTexture("assets/app-terrain/type-water-base-1024.png");
        //Assets.loadTexture("assets/app-terrain/road-1024.png");
        Assets.loadTexture("assets/app-terrain/road-new-1024.png");
        //Assets.loadTexture("assets/app-terrain/wheat-field-1024.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.loadTexture("assets/app-terrain/wheat-field-base.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.loadTexture("assets/app-terrain/wheat-field-lines.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);

        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadTexturePack("assets/app-texture-packs/icons.yml");
        Assets.loadTexturePack("assets/app-texture-packs/medieval-pack.yml");
        Assets.loadShader("outline", "assets/app-shaders/graphics-2d-shader-texture-outline.vert", "assets/app-shaders/graphics-2d-shader-texture-outline.frag");

        Assets.finishLoading();

        icons = Assets.get("assets/app-texture-packs/icons.yml");
        props = Assets.get("assets/app-texture-packs/medieval-pack.yml");

        //terrainWater = Assets.get("assets/app-terrain/type-water-base-1024.png");
        terrainWater = Assets.get("assets/app-terrain/water-1024.png");
        //terrainGrass = Assets.get("assets/app-terrain/grass-1024.png");
        terrainGrass = Assets.get("assets/app-terrain/grass-new-1024.png");
        //terrainRoad = Assets.get("assets/app-terrain/road-1024.png");
        terrainRoad = Assets.get("assets/app-terrain/road-new-1024.png");
        terrainWheatBase = Assets.get("assets/app-terrain/wheat-field-base.png");
        terrainWheatLines = Assets.get("assets/app-terrain/wheat-field-lines.png");

    }

    @Override
    public void start() {
        NodeToolBar toolBar = new NodeToolBar();

        NodeContainerHorizontal menuBarContainer = new NodeContainerHorizontal();
        menuBarContainer.boxWidthSizing = NodeContainer.Sizing.VIEWPORT;
        menuBarContainer.boxWidth = 1;
        menuBarContainer.boxHeightSizing = NodeContainer.Sizing.DYNAMIC;

        NodeMenuButton file = new NodeMenuButton("File");
        NodeMenuButton edit = new NodeMenuButton("Edit");
        NodeMenuButton help = new NodeMenuButton("Help");
        menuBarContainer.boxBackgroudColor = Color.valueOf("1D1D1D");
        menuBarContainer.boxBackgroundEnabled = true;
        menuBarContainer.boxBorderSize = 0;
        menuBarContainer.boxPaddingTop = 5;
        menuBarContainer.boxPaddingBottom = 5;
        menuBarContainer.boxPaddingLeft = 5;
        menuBarContainer.boxPaddingRight = 0;
        menuBarContainer.addChild(file);
        menuBarContainer.addChild(edit);
        menuBarContainer.addChild(help);

        toolbarWidget.anchor = Widget.Anchor.TOP_LEFT;
        toolbarWidget.anchorX = 0;
        toolbarWidget.anchorY = 24;
        toolbarWidget.addNode(toolBar);

        menuBarWidget.anchor = Widget.Anchor.TOP_CENTER;
        menuBarWidget.anchorY = 0;
        menuBarWidget.addNode(menuBarContainer);

        //Graphics.setContinuousRendering(false);
        //Graphics.setTargetFps(30);
        toolCastleGenerator = new ToolCastleGenerator(props);
        toolVillageGenerator = new ToolVillageGenerator(props);
        toolCityBlock = new ToolCityBlock(props);

        toolTerrainDeform = new ToolTerrainDeform(props);
        toolWheatField = new ToolWheatField(terrainWheatBase, terrainWheatLines);
        selectTool(toolCityBlock);
    }

    @Override
    public void update() {
        // update ui
        menuBarWidget.update(Graphics.getDeltaTime());
        menuBarWidget.handleInput(Graphics.getDeltaTime());
        toolbarWidget.update(Graphics.getDeltaTime());
        toolbarWidget.handleInput(Graphics.getDeltaTime());

        // update camera
        // CAMERA ZOOM
//        if (Input.mouse.getVerticalScroll() != 0) {
//            camera.zoom -= Input.mouse.getVerticalScroll() * 0.15f;
//        }
        if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && !Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.position.x -= 1.5f * Input.mouse.getXDelta();
            camera.position.y += 1.5f * Input.mouse.getYDelta();
            // TODO: set zoom limits
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.zoom += Input.mouse.getYDelta() * 0.05f;
        }
        screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        boolean leftJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftJustRelease = Input.mouse.isButtonJustReleased(Mouse.Button.LEFT);
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Math.abs(Input.mouse.getXDelta()) > 0 || Math.abs(Input.mouse.getYDelta()) > 0);

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            selectTool(toolTerrainPaint);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            selectTool(toolBrushTrees);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3)) {
            selectTool(toolCastleGenerator);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_4)) {
            selectTool(toolVillageGenerator);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_5)) {
            selectTool(toolCityBlock);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_6)) {
            selectTool(toolWheatField);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_7)) {
            selectTool(toolTerrainDeform);
        }


        // TODO
        if (toolTerrainPaint.active) {

            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
                toolTerrainPaint.mask = CommandTerrainPaint.WATER_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
                toolTerrainPaint.mask = CommandTerrainPaint.GRASS_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
                toolTerrainPaint.mask = CommandTerrainPaint.ROAD_MASK;
            }

            if (leftJustPressed || leftPressedAndMoved) {
                float x = screen.x;
                float y = screen.y;

                CommandTerrainPaint drawTerrainCommand = new CommandTerrainPaint();
                drawTerrainCommand.mask = toolTerrainPaint.mask;
                drawTerrainCommand.r = toolTerrainPaint.r;
                drawTerrainCommand.x = x;
                drawTerrainCommand.y = y;
                drawTerrainCommand.isAnchor = leftJustPressed;

                commandHistory.add(drawTerrainCommand);
            }
        }

        if (toolBrushTrees.active) {

            if (leftJustPressed || leftPressedAndMoved) {

                float x = screen.x;
                float y = screen.y;

                // TODO: control density.
                float dst = Vector2.dst(x, y, toolBrushTrees.lastCreatedX, toolBrushTrees.lastCreatedY);

                boolean regular = toolBrushTrees.species == MapTokenTree.Species.REGULAR;
                int baseIndex = regular ? MathUtils.randomUniformInt(0,6) : MathUtils.randomUniformInt(0,4);
                int trunkIndex = MathUtils.randomUniformInt(0,10);
                boolean fruits = toolBrushTrees.addFruits;

                CommandMapTokenCreateTree addTree = new CommandMapTokenCreateTree(toolBrushTrees.species, baseIndex, trunkIndex, fruits);
                addTree.x = x;
                addTree.y = y;
                float randomScale = MathUtils.randomUniformFloat(-toolBrushTrees.scaleRange, toolBrushTrees.scaleRange);
                addTree.sclX = toolBrushTrees.scale + randomScale;
                addTree.sclY = toolBrushTrees.scale + randomScale;
                addTree.isAnchor = leftJustPressed;
                toolBrushTrees.lastCreatedX = x;
                toolBrushTrees.lastCreatedY = y;

                commandHistory.add(addTree);

                // TODO: see if and how to use command.execute().
                MapTokenTree tree = new MapTokenTree(props, addTree.species, addTree.baseIndex, addTree.trunkIndex, addTree.withFruit);
                tree.setTransform(addTree);

                mapTokens.add(tree);
            }
        }

        if (toolCastleGenerator.active) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT)) { // hack undo
                toolCastleGenerator.toggleMode();
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) { // hack undo
                if (!mapTokens.isEmpty()) mapTokens.removeValue(toolCastleGenerator.lastCreated, true);
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER)) {
                Array<MapTokenCastleBlock> blocks = new Array<>();
                Vector2 cm = new Vector2();
                for (MapToken token : mapTokens) {
                    if (!(token instanceof MapTokenCastleBlock)) continue;
                    MapTokenCastleBlock block = (MapTokenCastleBlock) token;
                    blocks.add(block);
                    cm.add(block.x, block.y);
                }
                if (!blocks.isEmpty()) {
                    cm.scl(1f / blocks.size);
                    blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
                    System.out.println("<combination>");
                    for (MapTokenCastleBlock block : blocks) {
                        System.out.println("\t" + "<object type=\"" + block.type.ordinal() + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\"/>");
                    }
                    System.out.println("</combination>");
                }
            }
            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                toolCastleGenerator.selectNext();
            }
            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                float x = screen.x;
                float y = screen.y;

                if (toolCastleGenerator.mode == ToolCastleGenerator.Mode.SINGLES) {
                    MapTokenCastleBlock.BlockType type = toolCastleGenerator.currentType;
                    int baseIndex = toolCastleGenerator.baseIndex;

                    CommandMapTokenCreateCastleBlock addCastleBlock = new CommandMapTokenCreateCastleBlock(type, baseIndex);
                    addCastleBlock.x = x;
                    addCastleBlock.y = y;
                    addCastleBlock.sclX = toolCastleGenerator.scale;
                    if (addCastleBlock.type.isRight()) addCastleBlock.sclX *= -1;
                    addCastleBlock.sclY = toolCastleGenerator.scale;
                    addCastleBlock.isAnchor = leftJustPressed;
                    addCastleBlock.deg = MathUtils.randomUniformFloat(-3, 3);
                    commandHistory.add(addCastleBlock);

                    // TODO: see if and how to use command.execute().
                    MapTokenCastleBlock block = new MapTokenCastleBlock(props, type, addCastleBlock.baseIndex);
                    block.setTransform(addCastleBlock);

                    mapTokens.add(block);
                    toolCastleGenerator.lastCreated = block;
                } else {
                    ToolCastleGenerator.BlockUnit[] blocks = toolCastleGenerator.getCombination();
                    for (ToolCastleGenerator.BlockUnit b : blocks) {
                        CommandMapTokenCreateCastleBlock addCastleBlock = new CommandMapTokenCreateCastleBlock(b.type, 0);
                        addCastleBlock.x = x + b.offsetX * toolCastleGenerator.scale;
                        addCastleBlock.y = y + b.offsetY * toolCastleGenerator.scale;
                        addCastleBlock.sclX = toolCastleGenerator.scale;
                        if (b.type.isRight()) addCastleBlock.sclX *= -1;
                        addCastleBlock.sclY = toolCastleGenerator.scale;
                        addCastleBlock.deg = MathUtils.randomUniformFloat(-3, 3);
                        commandHistory.add(addCastleBlock);

                        MapTokenCastleBlock block = new MapTokenCastleBlock(props, b.type, MathUtils.randomUniformInt(0, 100));
                        block.setTransform(addCastleBlock);

                        mapTokens.add(block);
                    }
                }
            }
        }

        if (toolVillageGenerator.active) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT)) { // hack undo
                toolVillageGenerator.toggleMode();
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) { // hack undo
                if (!mapTokens.isEmpty()) mapTokens.removeValue(toolVillageGenerator.lastCreated, true);
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER)) {
                Array<MapToken> blocks = new Array<>();
                Vector2 cm = new Vector2();
                for (MapToken token : mapTokens) { // TODO: include props as well.
                    if (!(token instanceof MapTokenHouseVillage)) continue;
                    MapTokenHouseVillage block = (MapTokenHouseVillage) token;
                    blocks.add(block);
                    cm.add(block.x, block.y);
                }
                if (!blocks.isEmpty()) {
                    cm.scl(1f / blocks.size);
                    blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
                    System.out.println("<combination>");
                    for (MapToken b : blocks) {
                        MapTokenHouseVillage block = (MapTokenHouseVillage) b;
                        System.out.println("\t" + "<object type=\"" + block.type.ordinal() + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\"/>");
                    }
                    System.out.println("</combination>");
                }
            }
            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                toolVillageGenerator.selectNext();
            }
            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                float x = screen.x;
                float y = screen.y;

                if (toolVillageGenerator.mode == ToolVillageGenerator.Mode.SINGLES_HOUSES) {
                    MapTokenHouseVillage.HouseType type = toolVillageGenerator.currentHouseType;
                    int baseIndex = toolVillageGenerator.baseIndex;

                    CommandMapTokenCreateHouseVillage addVillageHouse = new CommandMapTokenCreateHouseVillage(type, baseIndex);
                    addVillageHouse.x = x;
                    addVillageHouse.y = y;
                    addVillageHouse.sclX = toolVillageGenerator.scale;
                    if (addVillageHouse.type.isRight()) addVillageHouse.sclX *= -1;
                    addVillageHouse.sclY = toolVillageGenerator.scale;
                    addVillageHouse.isAnchor = leftJustPressed;
                    addVillageHouse.deg = MathUtils.randomUniformFloat(-3, 3);
                    commandHistory.add(addVillageHouse);

                    // TODO: see if and how to use command.execute().
                    MapTokenHouseVillage house = new MapTokenHouseVillage(props, type, addVillageHouse.baseIndex);
                    house.setTransform(addVillageHouse);

                    mapTokens.add(house);
                    toolVillageGenerator.lastCreated = house;

                } else if (toolVillageGenerator.mode == ToolVillageGenerator.Mode.SINGLES_PROPS) {

                    int baseIndex = toolVillageGenerator.currentPropType;

                    CommandMapTokenCreatePropVillage addVillageProp = new CommandMapTokenCreatePropVillage(baseIndex);
                    addVillageProp.x = x;
                    addVillageProp.y = y;
                    addVillageProp.sclX = toolVillageGenerator.scale;
                    addVillageProp.sclY = toolVillageGenerator.scale;
                    addVillageProp.isAnchor = leftJustPressed;
                    addVillageProp.deg = MathUtils.randomUniformFloat(-3, 3);
                    commandHistory.add(addVillageProp);

                    // TODO: see if and how to use command.execute().
                    MapTokenPropVillage prop = new MapTokenPropVillage(props, addVillageProp.baseIndex);
                    prop.setTransform(addVillageProp);

                    mapTokens.add(prop);
                    toolVillageGenerator.lastCreated = prop;

                } else {
//                    ToolCastleGenerator.BlockUnit[] blocks = toolCastleGenerator.getCombination();
//                    for (ToolCastleGenerator.BlockUnit b : blocks) {
//                        CommandMapTokenCreateCastleBlock addCastleBlock = new CommandMapTokenCreateCastleBlock(b.type, 0);
//                        addCastleBlock.x = x + b.offsetX;
//                        addCastleBlock.y = y + b.offsetY;
//                        addCastleBlock.sclX = toolCastleGenerator.scale;
//                        if (b.type.isRight()) addCastleBlock.sclX *= -1;
//                        addCastleBlock.sclY = toolCastleGenerator.scale;
//                        addCastleBlock.deg = MathUtils.randomUniformFloat(-3, 3);
//                        commandHistory.add(addCastleBlock);
//
//                        MapTokenCastleBlock block = new MapTokenCastleBlock(props, b.type, MathUtils.randomUniformInt(0, 100));
//                        block.setTransform(addCastleBlock);
//
//                        mapTokens.add(block);
//                    }
                }
            }
        }

        if (toolCityBlock.active) {
            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                toolCityBlock.selectNextDirection();
            }
            if (Input.mouse.getVerticalScroll() > 0) {
                toolCityBlock.selectNextSize();
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_CONTROL)) {
                toolCityBlock.selectNextLook();
            }

            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                float x = screen.x;
                float y = screen.y;

                CommandMapTokenCreateHouseCity2 addCityHouse = new CommandMapTokenCreateHouseCity2(toolCityBlock.currentDirection, toolCityBlock.currentSize, toolCityBlock.currentLook);
                addCityHouse.x = x;
                addCityHouse.y = y;
                addCityHouse.sclX = toolCityBlock.scale * (toolCityBlock.currentDirection == MapTokenHouseCity2.Direction.RIGHT ? -1 : 1);
                addCityHouse.sclY = toolCityBlock.scale;
                addCityHouse.deg = 0;
                addCityHouse.isAnchor = true;
                commandHistory.add(addCityHouse);

                // TODO: see if and how to use command.execute().
                MapTokenHouseCity2 cityHouse = new MapTokenHouseCity2(props, toolCityBlock.currentDirection, toolCityBlock.currentSize, toolCityBlock.currentLook, 0, 0);
                cityHouse.setTransform(addCityHouse);

                mapTokens.add(cityHouse);
            }
        }

        if (toolTerrainDeform.active) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT)) {
                toolTerrainDeform.toggleType();
            }
            if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
                toolTerrainDeform.angle += Input.mouse.getYDelta();
                toolTerrainDeform.angle %= 360;
            }
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_CONTROL)) {
                toolTerrainDeform.selectNext();
            }
            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                float x = screen.x;
                float y = screen.y;

                CommandTerrainDeform addGroundFold = new CommandTerrainDeform(props, toolTerrainDeform.currentType, toolTerrainDeform.index);
                addGroundFold.x = x;
                addGroundFold.y = y;
                addGroundFold.sclX = toolTerrainDeform.scale * (toolTerrainDeform.currentType.isRight() ? -1 : 1);
                addGroundFold.sclY = toolTerrainDeform.scale;
                addGroundFold.deg = (toolTerrainDeform.currentType == CommandTerrainDeform.GroundType.LINE || toolTerrainDeform.currentType == CommandTerrainDeform.GroundType.BUMP_MIDDLE) ? toolTerrainDeform.angle : MathUtils.randomUniformFloat(-3,3);
                addGroundFold.isAnchor = true;
                commandHistory.add(addGroundFold);

                // NOTE: ground deformation objects are not map tokens.
            }
        }

        if (toolWheatField.active) {
            // TODO: "bug" here when rotating. on right mouse release, overview snaps in-and-out of position.
            if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
                toolWheatField.screenAnchorX = screen.x;
                toolWheatField.screenAnchorY = screen.y;
                toolWheatField.mouseAnchorX = Input.mouse.getX();
                toolWheatField.mouseAnchorY = Input.mouse.getY();
                toolWheatField.renderAtAnchor = true;
            }
            if (Input.mouse.isButtonJustReleased(Mouse.Button.RIGHT)) {
                Input.mouse.setCursorPosition(toolWheatField.mouseAnchorX, toolWheatField.mouseAnchorY);
            } else if (Input.mouse.isButtonReleased(Mouse.Button.RIGHT)) {
                toolWheatField.mouseAnchorX = Input.mouse.getX();
                toolWheatField.mouseAnchorY = Input.mouse.getY();
                toolWheatField.renderAtAnchor = false;
            }

            if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL) && Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
                toolWheatField.linesAngle += Input.mouse.getYDelta();
            } else if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
                toolWheatField.angle += Input.mouse.getYDelta();
            }

            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                float x = screen.x;
                float y = screen.y;

                toolWheatField.randomizeColor();

                CommandTerrainDrawWheat drawWheat = new CommandTerrainDrawWheat(toolWheatField.polygon, toolWheatField.currentColor);
                drawWheat.linesAngle = toolWheatField.linesAngle;
                drawWheat.x = x;
                drawWheat.y = y;
                drawWheat.deg = toolWheatField.angle;
                drawWheat.sclX = toolWheatField.scale;
                drawWheat.sclY = toolWheatField.scale;

                drawWheat.isAnchor = true;
                commandHistory.add(drawWheat);

                // NOTE: ground deformation objects are not map tokens.
            }
        }



        // get all terrain draw commands history
        commandsTerrainPaint.clear();
        commandsTerrainDeform.clear();
        commandsDrawWheat.clear();
        for (Command command : commandHistory) { // TODO: iterate until last index.
            if (command instanceof CommandTerrainPaint) {
                CommandTerrainPaint cmd = (CommandTerrainPaint) command;
                commandsTerrainPaint.add(cmd);
            } else if (command instanceof CommandTerrainDeform) {
                CommandTerrainDeform cmd = (CommandTerrainDeform) command;
                commandsTerrainDeform.add(cmd);
            } else if (command instanceof CommandTerrainDrawWheat) {
                CommandTerrainDrawWheat cmd = (CommandTerrainDrawWheat) command;
                commandsDrawWheat.add(cmd);
            }
        }

        // render scene
        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);

        // draw terrain
        // create terrain stencil mask
        renderer2D.beginStencil();
        renderer2D.stencilMaskClear(CommandTerrainPaint.GRASS_MASK);
        for (CommandTerrainPaint command : commandsTerrainPaint) {
            renderer2D.setStencilModeSetValue(command.mask);
            renderer2D.drawCircleFilled(command.r, command.refinement, command.x, command.y, command.deg, command.sclX, command.sclY);
        }
        renderer2D.endStencil();

        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.WATER_MASK);
        renderer2D.drawTexture(terrainWater, 0, 0, 0, 1, 1);
        renderer2D.disableMasking();
        // TODO: apply object outlining here. Maybe with stencil, maybe with another frame buffer.
        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.GRASS_MASK);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, 1);
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.ROAD_MASK);
        renderer2D.drawTexture(terrainRoad, 0, 0, 0, 1, 1);
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.GRASS_MASK);
        commandsTerrainDeform.sort(Comparator.comparing((CommandTerrainDeform t) -> t.type == CommandTerrainDeform.GroundType.LINE ? 0 : 1)
                .thenComparingInt(t -> -(int) t.y));
        renderer2D.setColor(1,1,1,0.5f);
        for (CommandTerrainDeform deform : commandsTerrainDeform) {
            if (deform.type != CommandTerrainDeform.GroundType.LINE) continue;
            renderer2D.drawTextureRegion(deform.region, deform.x, deform.y, deform.deg, deform.sclX, deform.sclY);
            renderer2D.drawTextureRegion(deform.region, deform.x, deform.y - 1, deform.deg, deform.sclX, deform.sclY);
        }

        // draw wheat fields here. they are masked by the ground.
        renderer2D.setColor(1,1,1,1);
        for (CommandTerrainDrawWheat wheatCommand : commandsDrawWheat) {
            // draw base
            renderer2D.setColor(wheatCommand.color);
            renderer2D.drawPolygonFilled(wheatCommand.polygon, terrainWheatBase, wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            // draw lines
            renderer2D.setColor(1,1,1,1);
            renderer2D.drawPolygonFilled(wheatCommand.polygon, terrainWheatLines, uv -> uv.rotateDeg(wheatCommand.linesAngle), wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            // draw outline
            renderer2D.setColor(wheatCommand.color.r * 0.5f, wheatCommand.color.g * 0.5f, wheatCommand.color.b * 0.5f, wheatCommand.color.a * 0.4f);
            renderer2D.drawCurveFilled(terrainWheatBase, 3, 5, wheatCommand.outline, wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            renderer2D.setColor(1,1,1,1);
        }
        renderer2D.disableMasking();

        renderer2D.setColor(Color.WHITE);
        for (CommandTerrainDeform deform : commandsTerrainDeform) {
            if (deform.type == CommandTerrainDeform.GroundType.LINE) continue;
            renderer2D.drawTextureRegion(deform.region, deform.x, deform.y, deform.deg, deform.sclX, deform.sclY); // base should never be null.
        }

        // draw map objects
        // TODO: first, calculate map items array
        mapTokens.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (MapToken token : mapTokens) {
            token.render(renderer2D);
        }

        // render tool overlay here
        //if (toolBrushTrees.active) toolBrushTrees.renderToolOverlay(renderer2D, screen.x, screen.y, 0, 1,1);
        if (activeTool != null) activeTool.renderToolOverlay(renderer2D, screen.x, screen.y, 0, 1,1);

        // center point
//        renderer2D.setColor(Color.RED);
//        renderer2D.drawCircleFilled(10, 25, 0,0,0,1,1);
//        renderer2D.setColor(Color.WHITE);

        renderer2D.end();

        // render tool-overlay
        renderer2D.begin();
        //if (toolBrushTrees.active) toolBrushTrees.renderToolOverlay(renderer2D, Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f, -Input.mouse.getY() + Graphics.getWindowHeight() * 0.5f, 0, 1,1);
        renderer2D.end();

        // render UI
        renderer2D.begin();
        toolbarWidget.draw(renderer2D);
        menuBarWidget.draw(renderer2D);
        renderer2D.end();

    }

    private void selectTool(Tool tool) {
        if (tool == null) {
            activeTool.active = false;
            activeTool = null;
            //Graphics.setCursorDefault(); // TODO
        } else if (activeTool == null) {
            activeTool = tool;
            activeTool.active = true;
            //Graphics.setCursorNone();
        } else if (activeTool != tool) { // already selected, do nothing
            activeTool.active = false;
            tool.active = true;
            activeTool = tool;
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
