package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScreenLoading extends ApplicationScreen {

    private ApplicationScreen screen = new SceneRendering2D_ECS_CTransform_1();

    @Override
    public void show() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = screen.getRequiredAssets();
        for (Map.Entry<String, Class<? extends MemoryResource>> requiredAsset : requiredAssets.entrySet()) {
            AssetStore.loadAsset(requiredAsset.getValue(), requiredAsset.getKey());
        }
    }

    @Override
    protected void refresh() {
        if (!AssetStore.isLoadingInProgress()) {
            Application.switchScreen(screen);
        }

        // frame update
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void hide() {

    }

    @Override
    public void deleteAll() {

    }

}
