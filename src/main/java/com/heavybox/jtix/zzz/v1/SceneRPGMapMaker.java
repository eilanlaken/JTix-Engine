package com.heavybox.jtix.zzz.v1;

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
import com.heavybox.jtix.zzz.*;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

public class SceneRPGMapMaker implements Scene {

    private static final Vector3 screen = new Vector3();
    private final Renderer2D renderer2D = new Renderer2D();

    /* assets */
    private TexturePack props;
    private Texture terrainWater;
    private Texture terrainGrass;
    private Texture terrainWheatBase;
    private Texture terrainWheatLines;

    // tools
    private Tool activeTool = null;
    private ToolTerrainMask toolTerrainMask;

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);
    public Array<Command> commandHistory = new Array<>(true, 10);
    public int commandChainIndex = -1;
    private final Array<CommandTerrainPaint> commandsTerrainPaint = new Array<>(true, 100);
    private final Array<CommandTerrainDrawWheat> commandsDrawWheat = new Array<>(true, 5);
    public final Array<MapToken> mapTokens = new Array<>(true, 10);
    // TODO: make a copy array of map tokens for clearing(), copying(), sorting() then rendering().

    @Override
    public void setup() {
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

        terrainWheatBase = Assets.get("assets/app-terrain/wheat-field-base.png");
        terrainWheatLines = Assets.get("assets/app-terrain/wheat-field-lines.png");

    }

    @Override
    public void start() {
        toolTerrainMask = new ToolTerrainMask(this);
        selectTool(toolTerrainMask);
    }

    @Override
    public void update() {
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
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            selectTool(toolTerrainMask);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_4)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_5)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_6)) {

        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_7)) {

        }

        // get all terrain draw commands history
        commandsTerrainPaint.clear();
        commandsDrawWheat.clear();
        for (Command command : commandHistory) { // TODO: iterate until last index.

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
        renderer2D.setMaskingFunctionEquals(CommandTerrainPaint.GRASS_MASK);

        // draw wheat fields here. they are masked by the ground.
        renderer2D.setColor(1,1,1,1);
        if (false) for (CommandTerrainDrawWheat wheatCommand : commandsDrawWheat) {
            // draw base
            renderer2D.setColor(wheatCommand.color);
            renderer2D.drawPolygonFilled(wheatCommand.polygon, terrainWheatBase, uv -> uv.rotateDeg(wheatCommand.linesAngle).scl(1), wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            // draw lines
            renderer2D.setColor(1,1,1,1);
            renderer2D.drawPolygonFilled(wheatCommand.polygon, terrainWheatLines, uv -> uv.rotateDeg(wheatCommand.linesAngle).scl(1), wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            // draw outline
            renderer2D.setColor(wheatCommand.color.r * 0.5f, wheatCommand.color.g * 0.5f, wheatCommand.color.b * 0.5f, wheatCommand.color.a * 0.4f);
            renderer2D.drawCurveFilled(terrainWheatBase, 3, 5, wheatCommand.outline, wheatCommand.x, wheatCommand.y, wheatCommand.deg, wheatCommand.sclX, wheatCommand.sclY);
            renderer2D.setColor(1,1,1,1);
        }
        renderer2D.disableMasking();
        renderer2D.setColor(Color.WHITE);
        // draw map objects
        // TODO: first, calculate map items array
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
