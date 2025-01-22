package com.heavybox.jtix;

import com.heavybox.jtix.z_deprecated.z_old_application.ApplicationScreen;
import com.heavybox.jtix.z_deprecated.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.z_deprecated.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.z_deprecated.z_old_input.Mouse;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_deprecated.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Input_Fix extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.4f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private Shader shaderYellow;

    public SceneRendering2D_Input_Fix() {
        renderer2DOld = new Renderer2D_old();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

    }

    float ay = 0;
    @Override
    public void refresh() {




        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);


        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            System.out.println("clicked");
        }

        if (Mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            System.out.println("just pressed");
        }


        renderer2DOld.begin(componentGraphicsCamera);
        renderer2DOld.setTint(red);
        //renderer2D.drawCircleFilled(1f, 1400, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, 1498, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, baseR + dr, 0, 0, 0,0,0,1,1);
        renderer2DOld.drawCircleFilled(1f, baseR + dr, x, y, 0,0,0,1,1);

        //renderer2D.drawCircleFilled(1f, 1498, -2, 0, 0,0,0,1,1);
        renderer2DOld.end();
    }

    float dx = 0;
    float dy = 0;
    int dr = 0;
    int baseR = 2000;
    float x, y;

    @Override

    public void resize(int width, int height) { }
    @Override
    public void hide() {
        renderer2DOld.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", Shader.class);

        return requiredAssets;
    }

}
