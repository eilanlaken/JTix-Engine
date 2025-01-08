package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.ui_2.Node;
import com.heavybox.jtix.ui_2.NodeText;
import com.heavybox.jtix.ui_2.Style;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_8 implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    Texture flower;

    TexturePack pack;

    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 70);

    NodeText nodeText1 = new NodeText("parent");
    NodeText nodeText2 = new NodeText("child");


    Array<Node> uiNodes = new Array<>();


    @Override
    public void setup() {


        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.finishLoading();

        nodeText2.style.position = Style.Position.RELATIVE;

    }

    @Override
    public void start() {

        pack = Assets.get("assets/atlases/spots.yml");
        flower = new Texture("assets/textures/flower.png");//Assets.get("assets/textures/flower.png");

        uiNodes.add(nodeText1);
        //uiNodes.add(nodeText2);
    }


    float scale = 1;
    int index = 0;

    StringBuffer text = new StringBuffer();

    float x = 0, y = 0, deg = 0;
    Vector2 v = new Vector2(1, 0);

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            //btn.text = "this is a new longer text";
        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
            text.append((char)  codepoint);
        }


        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            nodeText1.style.x += 1;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            nodeText1.style.x -= 1;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            deg += 1;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //GL11.glClearColor(0.129f, 0.129f, 0.129f,1);

//        btn.update();
//        btn.x = 200;

//        custom.x = x;
//        custom.y = y;
//        custom.deg = deg;

        for (Node node : uiNodes) {
            node.fixedUpdate(Graphics.getDeltaTime());
            node.handleInput();
        }

        nodeText1.boxRegionWidth = 200;
        nodeText1.boxRegionHeight = 150;

        nodeText1.style.width = 120;
        nodeText1.style.height = 50;

        nodeText1.style.paddingLeft = 0;
        nodeText1.style.paddingRight = 30;

        nodeText1.style.paddingTop = 5;
        nodeText1.style.paddingBottom = 10;

        nodeText1.style.marginBottom = 10;

        nodeText1.style.sizeWidth = Style.Size.GAS;
        nodeText1.style.sizeHeight = Style.Size.GAS;

        // render font
        renderer2D.begin();

        //renderer2D.drawStringLine(text.toString(), 64, aabb, true,0,0, true);

        //renderer2D.drawRectangleFilled(250,Graphics.getWindowHeight() * 0.9f,40,1,0,0,0,1,1);
        for (Node node : uiNodes) {
            node.draw(renderer2D);
        }

        renderer2D.setColor(Color.MAROON);
        renderer2D.drawRectangleBorder(200,150,2,0,0,0,1,1);

        renderer2D.end();

    }

    @Override
    public void finish() {

    }

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }

}