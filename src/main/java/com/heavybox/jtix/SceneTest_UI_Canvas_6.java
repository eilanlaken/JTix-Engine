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
import com.heavybox.jtix.widgets_4.NodeContainer;
import com.heavybox.jtix.widgets_4.NodeDebug;
import com.heavybox.jtix.widgets_4.NodeText;
import com.heavybox.jtix.widgets_4.Widget;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_Canvas_6 implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();


    private final Widget canvas = new Widget();
    NodeText text = new NodeText("hello node");
    NodeDebug rect1 = new NodeDebug(0,100);
    NodeDebug rect2 = new NodeDebug(0,200);

    NodeContainer container = new NodeContainer();
    // TODO

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.loadTexturePack("assets/atlases/pack.yml");

        Assets.finishLoading();


        //containerC.boxSizingWidth = NodeContainer.Sizing.VIEWPORT;
        container.boxWidth = 1f;
        container.addChild(rect1);
        container.addChild(rect2);
        container.addChild(text);
        //System.out.println(containerC.getContentWidth());
        container.boxBackgroudColor = Color.ROYAL.clone();


        canvas.addNode(container);
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

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            container.deg += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            container.deg -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.T)) {
            sclX *= 1.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Y)) {
            sclX *= 0.99f;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();
        //nodeDebug.draw(renderer2D);
        //slider.draw(renderer2D);

        //checkbox.draw(renderer2D);
        canvas.draw(renderer2D);

//        renderer2D.setColor(0,1,0,0.4f);
//        renderer2D.drawRectangleBorder(300,100, -10,
//                30, 10,
//                30, 2,
//                0, 2,
//                0, 2,
//                0,0,0,1,1);
//
//        renderer2D.setColor(1,0,0,0.4f);
//        renderer2D.drawRectangleFilled(300,100,
//                10, 10,
//                0, 2,
//                40, 20,
//                30, 2,
//                0,0,0,1,1);



//        phase += 0.1f;
//        renderer2D.setColor(0,1,0,0.4f);
//        renderer2D.drawFunctionFilled(
//                300, 55, 10, -MathUtils.PI_TWO, MathUtils.PI_TWO * 2, 55,
//                x -> MathUtils.sinRad(x + phase), // Apply phase shift
//                0, 0, 0, 1, 1
//        );

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
