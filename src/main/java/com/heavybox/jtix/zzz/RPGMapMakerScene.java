package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

public class RPGMapMakerScene implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();
    private TexturePack icons;

    private final Widget toolbar = new Widget();
    private final Widget menuBar = new Widget();

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadTexturePack("assets/app-texture-packs/icons.yml");
        Assets.finishLoading();

        icons = Assets.get("assets/app-texture-packs/icons.yml");

        ToolButton select = new ToolButton(icons.getRegion("assets/app-icons/select.png"), "Select", "press S");
        ToolButton terrain = new ToolButton(icons.getRegion("assets/app-icons/terrain.png"), "Terrain", "press T");
        ToolButton brush = new ToolButton(icons.getRegion("assets/app-icons/brush.png"), "Brush", "press B");
        ToolButton path = new ToolButton(icons.getRegion("assets/app-icons/path.png"), "Path", "press P");
        ToolButton text = new ToolButton(icons.getRegion("assets/app-icons/text.png"), "Text", "press T");
        ToolButton export = new ToolButton(icons.getRegion("assets/app-icons/export.png"), "Export", "press E");

        NodeContainerVertical toolbarContainer = new NodeContainerVertical();
        toolbarContainer.boxWidthSizing = NodeContainer.Sizing.DYNAMIC;
        toolbarContainer.boxHeightSizing = NodeContainer.Sizing.DYNAMIC;
        toolbarContainer.boxBorderSize = 0;
        toolbarContainer.boxPaddingTop = 5;
        toolbarContainer.boxPaddingBottom = 5;
        toolbarContainer.boxBackgroudColor = Color.valueOf("1D1D1D");
        toolbarContainer.boxBackgroundEnabled = true;
        toolbarContainer.addChild(select);
        toolbarContainer.addChild(terrain);
        toolbarContainer.addChild(brush);
        toolbarContainer.addChild(path);
        toolbarContainer.addChild(text);
        toolbarContainer.addChild(export);

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

        toolbar.anchor = Widget.Anchor.TOP_LEFT;
        toolbar.anchorX = 0;
        toolbar.anchorY = 40;
        toolbar.addNode(toolbarContainer);

        menuBar.anchor = Widget.Anchor.TOP_CENTER;
        menuBar.anchorY = 0;
        menuBar.addNode(menuBarContainer);
    }

    @Override
    public void start() {

    }

    float x = 0, y = 0, deg = 0, sclX = 1, sclY = 1;

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
//            text.append((char)  codepoint);
        }

        menuBar.update(Graphics.getDeltaTime());
        menuBar.handleInput(Graphics.getDeltaTime());
        toolbar.update(Graphics.getDeltaTime());
        toolbar.handleInput(Graphics.getDeltaTime());

        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {

        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        GL11.glClearColor(0.01f,0.01f,0.01f,1);


        // render font
        renderer2D.begin();
        //nodeDebug.draw(renderer2D);
        //slider.draw(renderer2D);

        //checkbox.draw(renderer2D);
        toolbar.draw(renderer2D);
        menuBar.draw(renderer2D);

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

}
