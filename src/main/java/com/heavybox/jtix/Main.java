package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationWindowAttributes;
import com.heavybox.jtix.graphics.TextureGenerator;
import com.heavybox.jtix.graphics.TexturePacker;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }


//        try {
//            TextureGenerator.generateTexturePack("assets/atlases", "marks", 2,0, TextureGenerator.TexturePackSize.SMALL_512,"assets/textures/mark-1.png", "assets/textures/mark-2.png", "assets/textures/mark-3.png");
//        } catch (Exception e) {
//            return;
//        }
//
//        if (true) return;

        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}