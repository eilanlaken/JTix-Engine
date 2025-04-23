package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.graphics.Renderer3D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;

public class ToolTreeStamp extends Tool {

    private static final int[] allowedIndices = {3};

    public Camera camera;
    public TreeType currentType = TreeType.BRIGHT_GREEN;
    public int currentIndex = allowedIndices[0];
    public GameObject tree;

    public Array<GameObject> gameObjects;

    public ToolTreeStamp(Camera camera, Array<GameObject> gameObjects) {
        this.camera = camera;
        this.gameObjects = gameObjects;

        this.tree = new GameObject();
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
            GameObject go = new GameObject();
            go.model = tree.model;
            go.transform = tree.transform.cpy();
            go.transform.rotateLocalAxisZ(MathUtils.randomUniformInt(0,360));
            gameObjects.add(go);
            currentIndex = allowedIndices[MathUtils.randomUniformInt(0, allowedIndices.length)];//MathUtils.randomUniformInt(0, 9);
            tree.model = getModel(currentType, currentIndex);
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
