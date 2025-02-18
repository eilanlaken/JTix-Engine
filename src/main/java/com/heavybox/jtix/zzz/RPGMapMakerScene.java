package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

public class RPGMapMakerScene implements Scene {

    private static final Vector3 screen = new Vector3();

    private final Renderer2D renderer2D = new Renderer2D();
    private TexturePack icons;

    private final Widget toolbarWidget = new Widget();
    private final Widget menuBarWidget = new Widget();

    private Texture terrainWater;
    private Texture terrainGrass;
    private Texture terrainRoad;
    private Texture terrainWheat;

    private final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);

    private final Array<Command> commands = new Array<>(true, 100);
    private final Array<CommandTerrain> commandsTerrain = new Array<>(true, 100);

    // active tool. TODO: make static constants of tool indices.
    private int activeTool = 1;
    int cmd_mask = CommandTerrain.GRASS_MASK;


    @Override
    public void setup() {
        // TODO: make the program CRASH and not thread-locked when file can't load.
        Assets.loadTexture("assets/app-textures/terrain-water-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-grass-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-road-1024.png");
        Assets.loadTexture("assets/app-textures/terrain-wheat-1024.png");

        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadTexturePack("assets/app-texture-packs/icons.yml");
        Assets.finishLoading();

        icons = Assets.get("assets/app-texture-packs/icons.yml");
        terrainWater = Assets.get("assets/app-textures/terrain-water-1024.png");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass-1024.png");
        terrainRoad = Assets.get("assets/app-textures/terrain-road-1024.png");
        terrainWheat = Assets.get("assets/app-textures/terrain-wheat-1024.png");

        ToolBar toolBar = new ToolBar();

        NodeContainerHorizontal menuBarContainer = new NodeContainerHorizontal();
        menuBarContainer.boxWidthSizing = NodeContainer.Sizing.VIEWPORT;
        menuBarContainer.boxWidth = 1;
        menuBarContainer.boxHeightSizing = NodeContainer.Sizing.DYNAMIC;

        MenuButton file = new MenuButton("File");
        MenuButton edit = new MenuButton("Edit");
        MenuButton help = new MenuButton("Help");
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

        if (activeTool == 1) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
                cmd_mask = CommandTerrain.WATER_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
                cmd_mask = CommandTerrain.GRASS_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
                cmd_mask = CommandTerrain.ROAD_MASK;
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.R)) {
                cmd_mask = CommandTerrain.WHEAT_MASK;
            }
            boolean leftJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
            boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Math.abs(Input.mouse.getXDelta()) > 0 || Math.abs(Input.mouse.getYDelta()) > 0);
            if (leftJustPressed || leftPressedAndMoved) {
                screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
                camera.unProject(screen);
                float x = screen.x;
                float y = screen.y;

                CommandTerrain drawTerrainCommand = new CommandTerrain();
                drawTerrainCommand.mask = cmd_mask;
                drawTerrainCommand.x = x;
                drawTerrainCommand.y = y;


//                CommandTerrain drawOutlineCommand = new CommandTerrain();
//                drawOutlineCommand.mask = CommandTerrain.DRAW_OUTLINE;
//                drawOutlineCommand.r = drawTerrainCommand.r + 2;
//                drawOutlineCommand.x = x;
//                drawOutlineCommand.y = y;
//                commands.add(drawOutlineCommand);

                commands.add(drawTerrainCommand);
            }
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        GL11.glClearColor(0.01f,0.01f,0.01f,1);

        // get all terrain draw commands history
        commandsTerrain.clear();
        for (Command command : commands) {
            if (command instanceof CommandTerrain) {
                CommandTerrain cmd = (CommandTerrain) command;
                commandsTerrain.add(cmd);
            }
        }
        //commandsTerrain.sort(Comparator.comparingInt(o -> o.mask)); TODO: need to sort by masking? No.

        // render scene
        renderer2D.begin(camera);

        // create terrain stencil mask
        renderer2D.beginStencil();
        renderer2D.stencilMaskClear(CommandTerrain.GRASS_MASK);
        for (CommandTerrain command : commandsTerrain) {
            renderer2D.setStencilModeSetValue(command.mask);
            renderer2D.drawCircleFilled(command.r, command.refinement, command.x, command.y, command.deg, command.sclX, command.sclY);
        }
        renderer2D.endStencil();

        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionEquals(CommandTerrain.WATER_MASK);
        renderer2D.drawTexture(terrainWater, 0, 0, 0, 1, 1);
        renderer2D.disableMasking();

        // TODO: apply object outlining here. Maybe with stencil, maybe with another frame buffer.
        renderer2D.enableMasking();
        // draw grass
        renderer2D.setMaskingFunctionEquals(CommandTerrain.GRASS_MASK);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, 1);

        renderer2D.setMaskingFunctionEquals(CommandTerrain.ROAD_MASK);
        renderer2D.drawTexture(terrainRoad, 0, 0, 0, 1, 1);

        renderer2D.setMaskingFunctionEquals(CommandTerrain.WHEAT_MASK);
        renderer2D.drawTexture(terrainWheat, 0, 0, 0, 1, 1);

        renderer2D.disableMasking();



//        renderer2D.enableMasking();
//        renderer2D.setMaskingFunctionEquals(CommandTerrain.GRASS_MASK);
//        for (CommandTerrain commandTerrain : commandsTerrain) {
//            if (commandTerrain.mask == CommandTerrain.ROAD_MASK) {
//                //renderer2D.drawTexture(terrainRoad, 0, 0, 0, 1, 1);
//            }
//            //renderer2D.drawTexture(terrainWheat, 0, 0, 0, 1, 1);
//        }
//        renderer2D.disableMasking();


        renderer2D.end();

        // render UI
        renderer2D.begin();
        toolbarWidget.draw(renderer2D);
        menuBarWidget.draw(renderer2D);
        renderer2D.end();

    }

    float phase;
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
}
