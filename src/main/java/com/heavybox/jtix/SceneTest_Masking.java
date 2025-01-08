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

public class SceneTest_Masking implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    Texture flower;
    TexturePack pack;

    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 70);


    @Override
    public void setup() {


        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.finishLoading();

    }

    @Override
    public void start() {

        pack = Assets.get("assets/atlases/spots.yml");
        flower = new Texture("assets/textures/flower.png");//Assets.get("assets/textures/flower.png");

    }


    @Override
    public void update() {



        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {

        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {

        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {

        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // render font
        renderer2D.begin();


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
