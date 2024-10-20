package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D_2;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_old_assets.AssetStore;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Renderer2D_2 extends ApplicationScreen {

    private Renderer2D_2 renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;
    float x, y;

    String vertexShader = """
                    #version 450

                    // attributes
                    layout(location = 0) in vec2 a_position;
                    layout(location = 1) in vec4 a_color;
                    layout(location = 2) in vec2 a_textCoords0;

                    // uniforms
                    uniform mat4 u_camera_combined;

                    // outputs
                    out vec4 color;
                    out vec2 uv;

                    void main() {
                        color = a_color;
                        uv = a_textCoords0;
                        gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);
                    };""";

    String fragmentShader = """
                    #version 450

                    // inputs
                    in vec4 color;
                    in vec2 uv;

                    // uniforms
                    uniform sampler2D u_texture;

                    // outputs
                    layout (location = 0) out vec4 out_color;

                    void main() {
                        out_color = color * texture(u_texture, uv);
                    }""";

    public SceneRendering2D_Renderer2D_2() {
        renderer2D = new Renderer2D_2();
        Shader shader = new Shader(vertexShader, fragmentShader);
        System.out.println();

        boolean x = false;
        Boolean a = (Boolean) x;
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");

    }

    @Override
    public void refresh() {

        componentGraphicsCamera.update();

        if (Keyboard.isKeyPressed(Keyboard.Key.X)) {
            componentGraphicsCamera.position.x -= 10;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Z)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.UP)) {
            y += 10;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.DOWN)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {

        }

        componentGraphicsCamera.update();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            //System.out.println(baseR + dr);
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;
        }

        renderer2D.begin(componentGraphicsCamera.lens.combined);

        renderer2D.drawTexture(yellowSquare,x,y,0,1,1);
        renderer2D.drawLineThin(0,0,400,400);

        renderer2D.end();
    }

    @Override

    public void resize(int width, int height) { }
    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();
        requiredAssets.put("assets/textures/yellowSquare.jpg", Texture.class);
        return requiredAssets;
    }

}
