package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;

public abstract class Tool {

    public static final float MAX_HEIGHT = 25.0f;
    public static final float MIN_HEIGHT = -MAX_HEIGHT;
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
        int i = (int) (((x + 256f) / TILE_SIZE) * 256f);
        int j = (int) (((256f - y) / TILE_SIZE) * 256f);
        // sample color
        Color color = heightMap.getPixelColor(i, j);
        float height = color.r;
        return MIN_HEIGHT + height * (MAX_HEIGHT - MIN_HEIGHT);
    }

    public void writeData(TerrainToken token, String type, String path) {
        token.userData.put("type", type);
        token.userData.put("path", path);
        token.userData.put("position", token.transform.getTranslation(new Vector3()));
        token.userData.put("rotation", token.transform.getRotation(new Quaternion()));
        token.userData.put("scale", token.transform.getScale(new Vector3()));
    }

    public abstract void render();
    public abstract void update();

}
