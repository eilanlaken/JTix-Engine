package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets.WidgetButton;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_5 implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    Texture flower;

    TexturePack pack;

    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 70);
    WidgetButton btn = new WidgetButton(220,140, "File");

    @Override
    public void setup() {
        Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        //Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");
        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);

        Assets.finishLoading();

        btn.style.paddingLeft = 10;
        btn.style.paddingRight = 10;

    }

    Vector2[] positions;
    Color[] colors;
    @Override
    public void start() {

        flower = new Texture("assets/textures/flower.png");//Assets.get("assets/textures/flower.png");


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
            btn.text = "this is a new longer text";
        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
            text.append((char)  codepoint);
        }



        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            x += 1;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            x -= 1;
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

        btn.frameUpdate(Graphics.getDeltaTime());
        btn.fixedUpdate(Graphics.getDeltaTime());


        // render font
        renderer2D.begin();

        //renderer2D.drawStringLine(text.toString(), 64, aabb, true,0,0, true);

        //renderer2D.drawRectangleFilled(250,Graphics.getWindowHeight() * 0.9f,40,1,0,0,0,1,1);

        btn.draw(renderer2D);



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
