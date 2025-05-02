package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.graphics.Renderer3D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;

public class ToolHouseStamp extends Tool {

    public HouseType currentType = HouseType.SMALL;
    public int currentIndex = 0;
    public TerrainToken house;

    public ToolHouseStamp(Camera camera, Array<TerrainToken> gameObjects, Texture heightMap) {
        super(camera, gameObjects, heightMap);
        this.house = new TerrainToken();
        this.house.transform = new Matrix4x4();
        this.house.model = getModel(currentType, currentIndex);
    }

    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        house.transform.setTranslation(screen.x, screen.y, 0);

        if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
            house.transform.rotateLocalAxisZ(Input.mouse.getYDelta());
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            TerrainToken token = new TerrainToken();
            token.model = house.model;
            token.transform = house.transform.cpy();
            Vector2 xy = new Vector2(token.transform.getPositionX(), token.transform.getPositionY());
            float z = getHeight(xy.x, xy.y);
            token.transform.translateGlobalAxisXYZ(0, 0, z);
            gameObjects.add(token);
            currentIndex = MathUtils.randomUniformInt(0, 6);
            house.model = getModel(currentType, currentIndex);
            writeData(token, HouseType.class.getSimpleName(), currentType.prefix + (currentIndex + 1) + ".fbx");
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentIndex = 0;
            if (currentType == HouseType.SMALL) currentType = HouseType.BIG;
            else currentType = HouseType.SMALL;
            house.model = getModel(currentType, currentIndex);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
            currentIndex = MathUtils.randomUniformInt(0, 6);
            house.model = getModel(currentType, currentIndex);
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < house.model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(house.model.meshes[i], house.model.materials[i], house.transform);
        }
    }

    public enum HouseType {
        SMALL("assets/app-models/rural-house-big_"),
        BIG("assets/app-models/rural-house-small_"),
        ;

        public final String prefix;

        HouseType(final String prefix) {
            this.prefix = prefix;
        }

    }

    private static Model getModel(HouseType type, int index) {
        final String path = type.prefix + (index + 1) + ".fbx";
        return Assets.get(path);
    }

}
