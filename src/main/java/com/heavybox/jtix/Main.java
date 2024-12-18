package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationWindowAttributes;
import com.heavybox.jtix.application_2.AppLogger;
import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.ApplicationSettings;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.async.AsyncTask;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.tools.ToolsFontGenerator;
import com.heavybox.jtix.tools.ToolsTextureGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.harfbuzz.hb_glyph_info_t;
import org.lwjgl.util.harfbuzz.hb_glyph_position_t;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.lwjgl.util.freetype.FreeType.*;
import static org.lwjgl.util.harfbuzz.HarfBuzz.*;

public class Main {

    public static void main(String[] args) throws IOException {


        try {
            //ToolsTextureGenerator.generateTextureMapNormal("assets/textures", "stoneN", "assets/textures/stones512.jpg", 0.5f,true);
        } catch (Exception e) {

        }

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }


        //TextureBuilder.buildTextureFont("assets/fonts", "bitmap", "assets/fonts/OpenSans-Italic.ttf", 32, false);
        //ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Regular.ttf", 13, true, null);
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


        //if (true) return;
        ApplicationSettings settings = new ApplicationSettings();
        settings.decorated = true;
        Application.init(settings); // can init with options.
        Application.launch(new SceneTest_Fonts_4());

    }



}