package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationWindowAttributes;
import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.async.AsyncTask;
import com.heavybox.jtix.tools.ToolsFontGenerator;

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
//        AsyncTask italic = new AsyncTask() {
//            @Override
//            public void task() {
//                ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Italic.ttf", 32, true, null);
//            }
//        };

//        AsyncTask regular = new AsyncTask() {
//            @Override
//            public void task() {
//                FontGenerator.generateBitmapFont("assets/fonts/OpenSans-Regular.ttf", 64, false, null);
//            }
//        };

        //AsyncTaskRunner.await(AsyncTaskRunner.async(italic));


//        try {
//            //TextureGenerator.generateTexturePack("assets/atlases", "spots", 2,2, TextureGenerator.TexturePackSize.SMALL_512,"assets/textures/red30x30.png", "assets/textures/green25x25.png", "assets/textures/blue100x100.png");
//        } catch (Exception e) {
//            return;
//        }

        //if (true) return;

//        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
//        Application.create();
//        Application.launch(new ScreenLoading());

        Application.init();
        Application.launch(new SceneTest_2());

    }

}