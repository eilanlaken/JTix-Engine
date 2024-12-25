package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Keyboard;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_2 implements Scene {


    private Renderer2D renderer2D = new Renderer2D();


    private String fontPath = "assets/fonts/OpenSans-Italic.ttf";

    Texture flower;

    Font aabb = new Font("assets/fonts/OpenSans-Regular.ttf");
    Font font;
    Font font2;

    TexturePack pack;


    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth() / 32f, Graphics.getWindowHeight() / 32f, 1, 0, 100, 70);


    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        //Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        //Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");
        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);

        Assets.finishLoading();

    }

    Vector2[] positions;
    Color[] colors;
    @Override
    public void start() {

        flower = Assets.get("assets/textures/flower.png");


        //font = Assets.get("assets/fonts/OpenSans-Regular.ttf");
        //font2 = Assets.get("C:\\Windows\\Fonts\\ahronbd.ttf");

        pack = Assets.get("assets/atlases/spots.yml");


        positions = new Vector2[40];
        colors = new Color[40];
        for (int i = 0; i < 40; i++) {
            positions[i] = new Vector2(MathUtils.randomUniformFloat(-640,640), MathUtils.randomUniformFloat(-480,480));
            colors[i] = new Color(MathUtils.randomUniformFloat(0f,1f), MathUtils.randomUniformFloat(0,1), MathUtils.randomUniformFloat(0,1), 1);
        }
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

        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
            text.append((char)  codepoint);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.A)) {
            index++;
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
            Graphics.setCursorResizeNESW();
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.R)) {
            Graphics.setCursorResizeNWSE();
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.T)) {
            Graphics.setCursorResizeAll();
        }

        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            Graphics.setCursorResizeVertical();

        }
        if (Input.mouse.isButtonClicked(Mouse.Button.MIDDLE)) {
            Graphics.setCursorNone();
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            x += 0.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            x -= 0.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            deg += 1;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
            v.rotateDeg(10);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //GL11.glClearColor(0.129f, 0.129f, 0.129f,1);


        // render font
        renderer2D.begin();

        // TODO: bug here.
        //renderer2D.drawStringLine(text.toString(), 20, font, true,0,0, true);
        //renderer2D.drawStringLine(text.toString(), 20, null, true,0,0, true);
        //renderer2D.drawStringLine(text.toString(), 64, font, true,0,0, true);
        renderer2D.drawStringLine(text.toString(), 64, aabb, true,0,0, true);

        //renderer2D.drawRectangleFilled(250,Graphics.getWindowHeight() * 0.9f,40,1,0,0,0,1,1);


        renderer2D.setColor(Color.WHITE);
        //renderer2D.setTexture(flower);




        //renderer2D.drawTexture(flower, 30, 10, 20,0,0,1,1);
        //renderer2D.drawTexture(flower, -5, 0, 0, 1,1);

//        renderer2D.drawTexture(flower, 0.5f,0.5f,1,1,
//                0,0,0,1,1);

        //renderer2D.drawTextureRegion(pack.getRegion("assets/textures/red30x30.png"), x,y,deg,1,1);

        //renderer2D.drawFunctionThin(300, -3, 3, 30, MathUtils::sinRad, 10,0,90,1,1);


        //renderer2D.drawFunctionFilled(400, 20, 10, -10, 10, 50, MathUtils::sinRad, 0,0,0,1,1);

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
