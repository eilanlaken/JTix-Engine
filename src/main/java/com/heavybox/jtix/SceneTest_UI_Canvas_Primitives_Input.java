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

public class SceneTest_UI_Canvas_Primitives_Input implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();


    private final Widget canvas = new Widget();
    Node checkbox = new NodeInputCheckbox();
    Node textField = new NodeInputTextField();
    Node radio = new NodeInputRadio();
    Node image;

    NodeContainer a = new NodeContainer();
    NodeCircle c = new NodeCircle(50);

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");
        Assets.loadTexturePack("assets/atlases/pack.yml");
        Assets.finishLoading();

        TexturePack t = Assets.get("assets/atlases/pack.yml");

        image = new NodeImage(t.getRegion("assets/textures/stones512.jpg"));

        checkbox.y = 300;
        textField.y = 0;
        image.y = -100;
        image.x = 200;
        ((NodeImage) image).resizeX = 0.1f;
        ((NodeImage) image).resizeY = 0.1f;

        radio.y = -300;

        a.boxBorderSize = 3;
        a.boxWidthSizing = NodeContainer.Sizing.STATIC;
        a.boxWidth = 300;
        a.boxHeightSizing = NodeContainer.Sizing.STATIC;
        a.boxHeight = 400;
        a.contentOverflowX = NodeContainer.Overflow.VISIBLE;
        a.contentOverflowY = NodeContainer.Overflow.HIDDEN;

        c.onClick = () -> System.out.println("circle clicked");

        a.addChild(c);
        canvas.addNode(a);

//        canvas.addNode(image);
//        canvas.addNode(checkbox);
//        canvas.addNode(radio);
//        canvas.addNode(textField);
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
            c.y += 3;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            c.y -= 3;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            c.x -= 3;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            c.x += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            a.deg -= 3;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            a.deg += 3;
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearColor(1f,1f,1f,1);


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

    static class NodeCircle extends Node {

        Color color = Color.YELLOW.clone();
        float r;

        NodeCircle(float r) {
            this.r = r;
        }

        @Override
        protected void fixedUpdate(float delta) {

        }

        @Override
        protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
            renderer2D.setColor(color);
            renderer2D.drawCircleFilled(r, 15, x, y, deg, sclX, sclY);
        }

        @Override
        public float calculateWidth() {
            return r * 2;
        }

        @Override
        public float calculateHeight() {
            return r * 2;
        }

        public void setPolygon(final Polygon polygon) {
            polygon.setToCircle(r, 15);
        }

    }

}
