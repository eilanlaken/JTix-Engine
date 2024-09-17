package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationWindowAttributes;
import com.heavybox.jtix.graphics.TextureBuilder;
import com.heavybox.jtix.tools.FontGenerator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }

        //TextureBuilder.buildTextureFont("assets/fonts", "bitmap", "assets/fonts/OpenSans-Italic.ttf", 32, false);
        //FontGenerator.generateBitmapFont("assets/fonts", "bitmap", "assets/fonts/OpenSans-Italic.ttf", 64);
        FontGenerator.generateBitmapFont("assets/fonts/OpenSans-Italic.ttf", 64, false, null);


//        try {
//            //TextureGenerator.generateTexturePack("assets/atlases", "spots", 2,2, TextureGenerator.TexturePackSize.SMALL_512,"assets/textures/red30x30.png", "assets/textures/green25x25.png", "assets/textures/blue100x100.png");
//        } catch (Exception e) {
//            return;
//        }

        if (true) return;

        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}