package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Texture;

public abstract class Tool {

    public static final float MAX_HEIGHT = 25.0f;
    public static final float MIN_HEIGHT = -25.0f;
    public static final float TILE_SIZE = 512;

    public Camera camera;
    public Array<TerrainToken> gameObjects;
    public Texture heightMap;

    public Tool(Camera camera, Array<TerrainToken> gameObjects, Texture heightMap) {
        this.camera = camera;
        this.gameObjects = gameObjects;
        this.heightMap = heightMap;
    }

    public float getHeight(float x, float y) {
        // convert to index:
        int i = (int) (((x + 256) / 512f) * 256);
        int j = (int) (((256 - y) / 512f) * 256);
        System.out.println(i + ", " + j);
        // sample color
        Color color = heightMap.getPixelColor(i, j);
        float height = color.r;
        return MIN_HEIGHT + height * (MAX_HEIGHT - MIN_HEIGHT);
    }

    public abstract void render();
    public abstract void update();

}
