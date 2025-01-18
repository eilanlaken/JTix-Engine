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
import com.heavybox.jtix.ui_3.NodeDebug;
import com.heavybox.jtix.ui_3.Style;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_Borders implements Scene {


    private Renderer2D renderer2D = new Renderer2D();

    private NodeDebug nodeDebug = new NodeDebug();

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.loadTexturePack("assets/atlases/pack.yml");

        Assets.finishLoading();

    }

    @Override
    public void start() {
        TexturePack pack = Assets.get("assets/atlases/pack.yml");
        TextureRegion region = pack.getRegion("assets/textures/stones512.jpg");
        System.out.println(region.u1);
        System.out.println(region.v1);

        System.out.println(region.u2);
        System.out.println(region.v2);

        nodeDebug.style.paddingLeft = 44;
        nodeDebug.style.paddingRight = 12;

        nodeDebug.style.cornerRadiusTopLeft = 40;
        nodeDebug.style.cornerSegmentsTopLeft = 20;

        nodeDebug.style.cornerRadiusTopRight = 10;
        nodeDebug.style.cornerSegmentsTopRight = 10;

        nodeDebug.style.cornerRadiusBottomRight = 10;
        nodeDebug.style.cornerSegmentsBottomRight = 20;

        nodeDebug.style.cornerRadiusBottomLeft = 100;
        nodeDebug.style.cornerSegmentsBottomLeft = 20;

        nodeDebug.style.sizingHeight = Style.Sizing.STATIC;
        nodeDebug.style.sizingWidth = Style.Sizing.STATIC;
        nodeDebug.style.width = 400;
        nodeDebug.style.height = 200;
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

        nodeDebug.update(Graphics.getDeltaTime());
        nodeDebug.handleInput();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            y += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            y -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            deg += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            deg -= 3;
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

        //renderer2D.drawRectangleBorder(300,100,20,0,0,0,1,1);
//        renderer2D.setColor(Color.GREEN);
//        renderer2D.drawRectangleThin(300,100,0,0,0,1,1);
//
//        renderer2D.setColor(Color.BLACK);
//        renderer2D.drawRectangleThin(300,100,
//                0, 2,
//                50, 30,
//                30, 0,
//                0, 10,
//                0,0,0,1,1);




//
//        renderer2D.setColor(1,0,0,0.3f);
//        renderer2D.drawCurveFilled(5, 22,
//                new Vector2(-100,20),
//                new Vector2(-90, -20),
//                new Vector2(-50,40),
//                new Vector2(-20,30),
//                new Vector2(0, 100),
//                new Vector2(22,-30)
//        );

//        renderer2D.setColor(Color.WHITE);
//        renderer2D.drawRectangleThin(300,100,
//                0, 10,
//                50, 2,
//                30, 2,
//                0, 2,
//                0,0,0,1,1);

        renderer2D.setColor(1,0,0,0.4f);
        renderer2D.drawRectangleBorder(300,100, -10,
                30, 10,
                0, 5,
                0, 2,
                0, 2,
                0,0,0,1,1);



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
