package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.zzz.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {


        ToolsTexturePacker.packTextures("assets/app-texture-packs", "icons", 2,2, ToolsTexturePacker.TexturePackSize.LARGE_2048,
                // toolbar
                "assets/app-icons/brush.png",
                "assets/app-icons/export.png",
                "assets/app-icons/path.png",
                "assets/app-icons/select.png",
                "assets/app-icons/terrain.png",
                "assets/app-icons/text.png",
                "assets/app-icons/move.png",
                // menu-bar
                "assets/app-icons/new.png",
                "assets/app-icons/open.png",
                "assets/app-icons/blank.png",
                "assets/app-icons/save.png",
                "assets/app-icons/exit.png"
                );
//        try {
//            //ToolsTextureGenerator.generateTextureMapNormal("assets/textures", "stoneN", "assets/textures/stones512.jpg", 0.5f,true);
//        } catch (Exception e) {
//
//        }

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }


        //TextureBuilder.buildTextureFont("assets/fonts", "bitmap", "assets/fonts/OpenSans-Italic.ttf", 32, false);
//        ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Regular.ttf", 25, true, "ABCD");
//        if (true) return;
        //ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Regular.ttf", 24, true, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
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


        ApplicationSettings settings = new ApplicationSettings();
        settings.decorated = true;
        Application.init(settings); // can init with options.
        Application.launch(new RPGMapMakerScene());

    }



}