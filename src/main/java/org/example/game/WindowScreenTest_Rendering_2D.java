package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_Rendering_2D extends WindowScreen {

    private Renderer2D renderer2D;
    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    public WindowScreenTest_Rendering_2D() {
        this.renderer2D = new Renderer2D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(20,20);

    }

    @Override
    public void show() {

    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);



    }

    @Override
    public void resize(int width, int height) { }


    @Override
    public void hide() {
        shader.free();
    }

    @Override
    public void free() {

    }

}
