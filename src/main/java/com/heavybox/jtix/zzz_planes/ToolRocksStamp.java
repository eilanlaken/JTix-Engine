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

public class ToolRocksStamp extends Tool {

    public RockType currentType = RockType.BLACK;
    public int currentIndex = 0;
    public TerrainToken rock;
    public float scale = 1;

    public ToolRocksStamp(Camera camera, Array<TerrainToken> gameObjects, Texture heightMap) {
        super(camera, gameObjects, heightMap);
        this.rock = new TerrainToken();
        this.rock.transform = new Matrix4x4();
        this.rock.model = getModel(currentType, currentIndex);
    }

    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        rock.transform.setTranslation(screen.x, screen.y, 0);

        if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
            scale += Math.signum(-Input.mouse.getYDelta()) * 0.1f;
            System.out.println(scale);
            Vector3 currentScale = rock.transform.getScale(new Vector3());
            rock.transform.scale(scale / currentScale.x, scale / currentScale.y, scale / currentScale.z);
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            TerrainToken token = new TerrainToken();
            token.model = rock.model;
            token.transform = rock.transform.cpy();
            Vector2 xy = new Vector2(token.transform.getPositionX(), token.transform.getPositionY());
            float z = getHeight(xy.x, xy.y);
            token.transform.translateGlobalAxisXYZ(0, 0, z);
            token.transform.rotateLocalAxisZ(MathUtils.randomUniformInt(0,360));
            gameObjects.add(token);
            currentIndex = MathUtils.randomUniformInt(0, 4);
            rock.model = getModel(currentType, currentIndex);
            writeData(token, RockType.class.getSimpleName(), currentType.prefix + (currentIndex + 1) + ".fbx");
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentIndex = 0;
            if (currentType == RockType.BLACK) currentType = RockType.GREY;
            else if (currentType == RockType.GREY) currentType = RockType.WHITE;
            else currentType = RockType.BLACK;
            rock.model = getModel(currentType, currentIndex);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
            currentIndex = MathUtils.randomUniformInt(0, 4);
            rock.model = getModel(currentType, currentIndex);
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < rock.model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(rock.model.meshes[i], rock.model.materials[i], rock.transform);
        }
    }

    public enum RockType {
        BLACK("assets/app-models/rocks-black_"),
        GREY("assets/app-models/rocks-grey_"),
        WHITE("assets/app-models/rocks-white_"),
        ;

        public final String prefix;

        RockType(final String prefix) {
            this.prefix = prefix;
        }

    }

    private static Model getModel(RockType type, int index) {
        final String path = type.prefix + (index + 1) + ".fbx";
        return Assets.get(path);
    }

}
