package com.heavybox.jtix.zzz_planes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class ToolCloudAtlasGenerator {

    public static final String directory = "C:\\Users\\eilan\\OneDrive\\Desktop\\Eilan\\Assets\\engine-development\\clouds_512_hd";

    public static void run() {
        File dir = new File(directory);
        File[] files = dir.listFiles();

        BufferedImage atlas = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlas.createGraphics();

        //Arrays.sort(files); // optional: ensure consistent order

        try {
            int index = 0;
            for (File file : files) {
                BufferedImage img = ImageIO.read(file);
                int x = (index % 8) * 256;
                int y = (index / 8) * 256;
                g.drawImage(img, x, y, null);
                index++;
            }
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        try {
            ImageIO.write(atlas, "png", new File("atlas.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
