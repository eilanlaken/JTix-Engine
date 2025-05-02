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
import com.heavybox.jtix.math.*;

public class ToolTreeStamp extends Tool {

    public static final float BRUSH_SIZE = 15;
    public static final int TREES_MAX_FLUX = 15;

    private static final int[] allowedIndices = {3};

    public TreeType currentType = TreeType.BRIGHT_GREEN;
    public int currentIndex = allowedIndices[0];
    public TerrainToken tree;

    public Array<Vector2> occupied = new Array<>();

    public ToolTreeStamp(Camera camera, Array<TerrainToken> gameObjects, Texture heightMap) {
        super(camera, gameObjects, heightMap);
        this.camera = camera;
        this.gameObjects = gameObjects;

        this.tree = new TerrainToken();
        this.tree.transform = new Matrix4x4();
        this.tree.model = getModel(currentType, currentIndex);
    }

    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        tree.transform.setTranslation(screen.x, screen.y, 0);

        if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
            tree.transform.rotateLocalAxisZ(Input.mouse.getYDelta());
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            // place tree
            for (int i = 0; i < TREES_MAX_FLUX; i++) {
                Vector2 middle = new Vector2(tree.transform.getPositionX(), tree.transform.getPositionY());
                Vector2 placement = new Vector2(middle.x + MathUtils.randomUniformFloat(-BRUSH_SIZE / 2, BRUSH_SIZE / 2),
                        middle.y + MathUtils.randomUniformFloat(-BRUSH_SIZE / 2, BRUSH_SIZE / 2));
                // check if position is occupied
                boolean positionOccupied = false;
                for (Vector2 position : occupied) {
                    positionOccupied |= Vector2.dst(position, placement) < 1.5f;
                }
                if (positionOccupied) {
                    continue;
                }

                Vector2 offset = new Vector2(placement.x - middle.x, placement.y - middle.y);
                TerrainToken token = new TerrainToken();
                token.model = tree.model;
                token.transform = tree.transform.cpy();
                token.transform.rotateLocalAxisZ(MathUtils.randomUniformInt(0,360));
                token.transform.translateGlobalAxisXYZ(offset.x, offset.y, 0);
                Vector2 xy = new Vector2(token.transform.getPositionX(), token.transform.getPositionY());
                float z = getHeight(xy.x, xy.y);
                token.transform.translateGlobalAxisXYZ(0, 0, z);
                writeData(token, TreeType.class.getSimpleName(), currentType.prefix + (currentIndex + 1) + ".fbx");
//                token.userData.put("type", TreeType.class.getSimpleName());
//                token.userData.put("path", currentType.prefix + (currentIndex + 1) + ".fbx");
//                token.userData.put("position", token.transform.getPosition(new Vector3()));
//                token.userData.put("rotation", token.transform.getRotation(new Quaternion()));
                gameObjects.add(token);
                currentIndex = allowedIndices[MathUtils.randomUniformInt(0, allowedIndices.length)];//MathUtils.randomUniformInt(0, 9);
                tree.model = getModel(currentType, currentIndex);
                Vector2 placed = new Vector2(token.transform.getPositionX(), token.transform.getPositionY());
                occupied.add(placed);
            }


        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentIndex = 0;
            int nextOrdinal = (currentType.ordinal() + 1) % TreeType.values().length;
            currentType = TreeType.values()[nextOrdinal];
            tree.model = getModel(currentType, currentIndex);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
            currentIndex = allowedIndices[MathUtils.randomUniformInt(0, allowedIndices.length)];//MathUtils.randomUniformInt(0, 9);
            tree.model = getModel(currentType, currentIndex);
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < tree.model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(tree.model.meshes[i], tree.model.materials[i], tree.transform);
        }
    }

    public enum TreeType {
        BRIGHT_GREEN("assets/app-models/tree-bright-green-flowers_"),
        BROWN_RED("assets/app-models/tree-brown-red-flowers_"),
        DARK_GREEN("assets/app-models/tree-dark-green-flowers_"),
        GREEN_ORANGE("assets/app-models/tree-green-orange-flowers_"),
        GREEN_RED("assets/app-models/tree-green-red-flowers_"),
        LIME_GREEN("assets/app-models/tree-lime-green-flowers_"),
        OLIVE_GREEN("assets/app-models/tree-olive-green-flowers_"),
        PURPLE_RED("assets/app-models/tree-purple-red-flowers_"),
        ;

        public final String prefix;

        TreeType(final String prefix) {this.prefix = prefix;}
    }

    private static Model getModel(TreeType type, int index) {
        final String path = type.prefix + (index + 1) + ".fbx";
        return Assets.get(path);
    }

}
