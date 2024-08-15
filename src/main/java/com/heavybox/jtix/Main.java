package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationWindowAttributes;
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
//            TexturePacker.Options options = new TexturePacker.Options("assets/atlases", "marks",
//                    null, null, null, null,
//                    0,0, TexturePacker.Options.Size.MEDIUM_1024);
//            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
//            //TexturePacker.packTextures(options, "assets/textures/physicsCircle.png", "assets/textures/physicsSquare.png");
//            TexturePacker.packTextures(options, "assets/textures/mark-1.png", "assets/textures/mark-2.png", "assets/textures/mark-3.png");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //if (true) return;

        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}