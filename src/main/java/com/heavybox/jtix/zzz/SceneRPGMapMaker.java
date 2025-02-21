package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

public class SceneRPGMapMaker implements Scene {

    private static final Vector3 screen = new Vector3();

    private final Renderer2D renderer2D = new Renderer2D();
    private TexturePack icons;
    private TexturePack props;

    private final Widget toolbarWidget = new Widget();
    private final Widget menuBarWidget = new Widget();

    private Texture terrainWater;
    private Texture terrainGrass;
    private Texture terrainRoad;
//    private Texture terrainWheat;

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);

    private final Array<Command> commands = new Array<>(true, 100);
    private final Array<CommandTerrainPaint> commandsTerrain = new Array<>(true, 100);
    private final Array<Command> commandsPutObjects = new Array<>(true, 100);

    // active tool. TODO: make static constants of tool indices.
    int cmd_mask = CommandTerrainPaint.GRASS_MASK;

    //private Tool activeTool = Tool.BRUSH;

    private Tool activeTool = null;
    private Tool toolTerrain = new ToolTerrain();
    private Tool toolBrushTrees = new ToolBrushTrees();

    // edit
    public Array<Command> commandChain = new Array<>(true, 10);
    public int commandChainIndex = -1;

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

                    "assets/app-rural/rural_house_1.png",
                    "assets/app-rural/rural_house_2.png",
                    "assets/app-rural/rural_house_3.png",
                    "assets/app-rural/rural_house_4.png",
                    "assets/app-rural/rural_house_5.png",
                    "assets/app-rural/rural_house_6.png",
                    "assets/app-rural/rural_house_7.png",
                    "assets/app-rural/rural_house_8.png",
                    "assets/app-rural/rural_house_9.png",
                    "assets/app-rural/rural_house_10.png",
                    "assets/app-rural/rural_house_11.png",
                    "assets/app-rural/rural_house_12.png",
                    "assets/app-rural/rural_house_13.png",
                    "assets/app-rural/rural_house_14.png",
                    "assets/app-rural/rural_house_15.png",
                    "assets/app-rural/rural_house_16.png",
                    "assets/app-rural/rural_house_17.png",
                    "assets/app-rural/rural_house_18.png",
                    "assets/app-rural/rural_house_19.png",
                    "assets/app-rural/rural_house_20.png",
                    "assets/app-rural/rural_house_21.png",
                    "assets/app-rural/rural_house_22.png",
                    "assets/app-rural/rural_house_23.png",
                    "assets/app-rural/rural_house_24.png",
                    "assets/app-rural/rural_house_25.png",
                    "assets/app-rural/rural_prop_fence_bar.png",
                    "assets/app-rural/rural_prop_fence_post.png",
                    "assets/app-rural/rural_prop_log_1.png",
                    "assets/app-rural/rural_prop_log_2.png",
                    "assets/app-rural/rural_prop_log_3.png",
                    "assets/app-rural/rural_prop_trunk_chopped.png",
                    "assets/app-rural/rural_prop_well.png",
                    "assets/app-rural/rural_prop_windmill.png",
                    "assets/app-rural/rural_tower_1.png",
                    "assets/app-rural/rural_tower_2.png",
                    "assets/app-rural/rural_tower_3.png",
                    "assets/app-rural/rural_tower_4.png",
                    "assets/app-rural/rural_tower_5.png",

                    "assets/app-city/city-house_1.png",
                    "assets/app-city/city-house_2.png",
                    "assets/app-city/city-house_3.png",
                    "assets/app-city/city-house_4.png",
                    "assets/app-city/city-house_5.png",
                    "assets/app-city/city-house_6.png",
                    "assets/app-city/city-house_7.png",
                    "assets/app-city/city-house_8.png",
                    "assets/app-city/city-house_9.png",
                    "assets/app-city/city-house_10.png",
                    "assets/app-city/city-house_11.png",
                    "assets/app-city/city-house_12.png",
                    "assets/app-city/city-house_13.png",
                    "assets/app-city/city-house_14.png",
                    "assets/app-city/city-house_15.png",
                    "assets/app-city/city-house_16.png",
                    "assets/app-city/city-house_17.png",
                    "assets/app-city/city-house_18.png",
                    "assets/app-city/city-house_19.png",
                    "assets/app-city/city-house_20.png",
                    "assets/app-city/city-house_21.png",
                    "assets/app-city/city-house_22.png",
                    "assets/app-city/city-house_23.png",
                    "assets/app-city/city-house_24.png",
                    "assets/app-city/city-house_25.png",
                    "assets/app-city/city-house_26.png",
                    "assets/app-city/city-house_27.png",
                    "assets/app-city/city-house_28.png",
                    "assets/app-city/city-house_29.png",
                    "assets/app-city/city-house_30.png",
                    "assets/app-city/city-house_31.png",
                    "assets/app-city/city-house_32.png",

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
                    "assets/app-castles/castle-wall-front-block_8.png"
            );
        } catch (Exception ignored) {} // PACK MEDIEVAL MAP PROPS

        // TODO: make the program CRASH and not thread-locked when file can't load.
        Assets.loadTexture("assets/app-textures/terrain-water-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-grass-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-road-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-wheat-1024.png");

        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadTexturePack("assets/app-texture-packs/icons.yml");
        Assets.loadTexturePack("assets/app-texture-packs/medieval-pack.yml");
        Assets.finishLoading();

        icons = Assets.get("assets/app-texture-packs/icons.yml");
        props = Assets.get("assets/app-texture-packs/medieval-pack.yml");

        terrainWater = Assets.get("assets/app-textures/terrain-water-1024.png");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass-1024.png");
        terrainRoad = Assets.get("assets/app-textures/terrain-road-1024.png");
        //terrainWheat = Assets.get("assets/app-textures/terrain-wheat-1024.png");

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
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
//            text.append((char)  codepoint);
        }

        menuBarWidget.update(Graphics.getDeltaTime());
        menuBarWidget.handleInput(Graphics.getDeltaTime());
        toolbarWidget.update(Graphics.getDeltaTime());
        toolbarWidget.handleInput(Graphics.getDeltaTime());


        if (Input.mouse.getVerticalScroll() != 0) {
            camera.zoom -= Input.mouse.getVerticalScroll() * 0.15f;
        }
        if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            camera.position.x -= 1.5f * Input.mouse.getXDelta();
            camera.position.y += 1.5f * Input.mouse.getYDelta();
            // TODO: set zoom limits
        }

        boolean leftJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Math.abs(Input.mouse.getXDelta()) > 0 || Math.abs(Input.mouse.getYDelta()) > 0);

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.O)) {
            selectTool(toolTerrain);
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.P)) {
            selectTool(toolBrushTrees);
        }

        if (toolTerrain.active) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
                cmd_mask = CommandTerrainPaint.WATER_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
                cmd_mask = CommandTerrainPaint.GRASS_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
                cmd_mask = CommandTerrainPaint.ROAD_MASK;
            }

            if (leftJustPressed || leftPressedAndMoved) {
                screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
                camera.unProject(screen);
                float x = screen.x;
                float y = screen.y;

                CommandTerrainPaint drawTerrainCommand = new CommandTerrainPaint();
                drawTerrainCommand.mask = cmd_mask;
                drawTerrainCommand.x = x;
                drawTerrainCommand.y = y;

                commands.add(drawTerrainCommand);
            }
        }

        if (toolBrushTrees.active) {
            if (leftJustPressed || leftPressedAndMoved) {
                screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
                camera.unProject(screen);
                float x = screen.x;
                float y = screen.y;

                CommandBrush commandBrush = new CommandBrush();
                commandBrush.x = x;
                commandBrush.y = y;
                commandBrush.treeType = MathUtils.randomUniformInt(1,6);
                commandBrush.trunkType = MathUtils.randomUniformInt(1,11);
                commandBrush.tree = props.getRegion("assets/app-trees/tree_regular_3.png"); // TODO
                commandBrush.trunk = props.getRegion("assets/app-trees/tree_regular_trunk_3.png"); // TODO
                commands.add(commandBrush);
            }
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        GL11.glClearColor(0.01f,0.01f,0.01f,1);

        // get all terrain draw commands history
        commandsTerrain.clear();
        for (Command command : commands) {
            if (command instanceof CommandTerrainPaint) {
                CommandTerrainPaint cmd = (CommandTerrainPaint) command;
                commandsTerrain.add(cmd);
            }
        }
        //commandsTerrain.sort(Comparator.comparingInt(o -> o.mask)); TODO: need to sort by masking? No.

        commandsPutObjects.clear();
        for (Command command : commands) {
            if (command instanceof CommandBrush) {
                CommandBrush cmd = (CommandBrush) command;
                commandsPutObjects.add(cmd);
            }
        }
        // sort by y value.

        // render scene
        renderer2D.begin(camera);

        // create terrain stencil mask
        renderer2D.beginStencil();
        renderer2D.stencilMaskClear(CommandTerrainPaint.GRASS_MASK);
        for (CommandTerrainPaint command : commandsTerrain) {
            renderer2D.setStencilModeSetValue(command.mask);
            renderer2D.drawCircleFilled(command.r, command.refinement, command.x, command.y, command.deg, command.sclX, command.sclY);
        }
        renderer2D.endStencil();

        // draw terrain
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
        renderer2D.disableMasking();

        // draw map objects
        // TODO: first, calculate map items array
        for (Command command : commandsPutObjects) {
            if (command instanceof CommandBrush) {
                CommandBrush drawObjectCommand = (CommandBrush) command;
                renderer2D.drawTextureRegion(drawObjectCommand.tree, drawObjectCommand.x, drawObjectCommand.y, drawObjectCommand.deg, drawObjectCommand.sclX, drawObjectCommand.sclY);
                renderer2D.drawTextureRegion(drawObjectCommand.trunk, drawObjectCommand.x, drawObjectCommand.y, drawObjectCommand.deg, drawObjectCommand.sclX, drawObjectCommand.sclY);
            }
        }
        renderer2D.end();

        // render tool-overlay
        renderer2D.begin();

        renderer2D.end();

        // render UI
        renderer2D.begin();
        toolbarWidget.draw(renderer2D);
        menuBarWidget.draw(renderer2D);
        renderer2D.end();

    }

    private void selectTool(Tool tool) {
        if (activeTool == null) {
            activeTool = tool;
            activeTool.active = true;
        } else if (activeTool != tool) { // already selected, do nothing
            activeTool.active = false;
            tool.active = true;
            activeTool = tool;
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
    }

//    public enum Tool {
//
//        SELECT,
//        TERRAIN,
//        BRUSH,
//        PATH,
//        TEXT,
//        EXPORT
//
//    }

    // represents a map object:
    // a tree + trunk + fruits, text, house, etc.
    // TODO: add a bounding rectangle for selection etc.
    public abstract static class MapItem {



    }
}
