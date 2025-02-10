package com.heavybox.jtix;

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

public class SceneTest_UI_Canvas_Primitives implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();


    private final Widget canvas = new Widget();
    NodeText text = new NodeText("hello node");
    NodeInputCheckbox checkbox = new NodeInputCheckbox();

    NodeContainerHorizontal container = new NodeContainerHorizontal();

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.loadTexturePack("assets/atlases/pack.yml");

        Assets.finishLoading();

        container.boxHeightSizing = NodeContainer.Sizing.DYNAMIC;
        container.boxBorderSize = 4;

        container.boxPaddingLeft = 50;
        container.boxPaddingRight = 50;

        //container.addChild(text);
        //System.out.println(containerC.getContentWidth());
        container.boxBackgroudColor = Color.ROYAL.clone();

        container.addChild(checkbox);

        text.x = 300;
        canvas.addNode(container);
        canvas.addNode(text);
    }

    @Override
    public void start() {
        TexturePack pack = Assets.get("assets/atlases/pack.yml");
        TextureRegion region = pack.getRegion("assets/textures/stones512.jpg");


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

        canvas.update(Graphics.getDeltaTime());
        canvas.handleInput(Graphics.getDeltaTime());

        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            container.y += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            container.y -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            container.x -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            container.x += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            checkbox.deg += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            checkbox.deg -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.T)) {
            checkbox.sclY *= 1.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Y)) {
            checkbox.sclY *= 0.99f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.P)) {
            checkbox.size *= 1.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.O)) {
            checkbox.size *= 0.99f;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();
        //nodeDebug.draw(renderer2D);
        //slider.draw(renderer2D);

        //checkbox.draw(renderer2D);
        canvas.draw(renderer2D);

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
