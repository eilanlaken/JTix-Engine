package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets.*;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_Canvas implements Scene {


    private Renderer2D renderer2D = new Renderer2D();

    private NodeDebug nodeDebug = new NodeDebug();
    private NodeSlider slider = new NodeSlider();
    private NodeCheckbox checkbox = new NodeCheckbox();
    private NodeRadioButton radioButton = new NodeRadioButton();

    private Canvas canvas = new Canvas();

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

        nodeDebug.style.boxPaddingLeft = 44;
        nodeDebug.style.boxPaddingRight = 12;

        nodeDebug.style.boxCornerRadiusTopLeft = 40;
        nodeDebug.style.boxCornerSegmentsTopLeft = 20;

        nodeDebug.style.boxCornerRadiusTopRight = 10;
        nodeDebug.style.boxCornerSegmentsTopRight = 10;

        nodeDebug.style.boxCornerRadiusBottomRight = 10;
        nodeDebug.style.boxCornerSegmentsBottomRight = 20;

        nodeDebug.style.boxCornerRadiusBottomLeft = 100;
        nodeDebug.style.boxCornerSegmentsBottomLeft = 20;

        nodeDebug.style.sizingHeight = Style.Sizing.ABSOLUTE;
        nodeDebug.style.sizingWidth = Style.Sizing.ABSOLUTE;
        nodeDebug.style.sizeWidth = 400;
        nodeDebug.style.sizeHeight = 200;
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

        canvas.fixedUpdate(Graphics.getDeltaTime());
        canvas.handleInput(Graphics.getDeltaTime());

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
        GL11.glClearColor(1f,1f,1f,1);


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
