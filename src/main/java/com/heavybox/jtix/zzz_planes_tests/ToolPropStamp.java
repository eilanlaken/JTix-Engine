package com.heavybox.jtix.zzz_planes_tests;

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

public class ToolPropStamp extends Tool {

    private static final int[] allowedIndices = {3};

    public PropType currentType = PropType.values()[0];
    public int currentIndex = allowedIndices[0];
    public TerrainToken prop;

    public ToolPropStamp(Camera camera, Array<TerrainToken> gameObjects, Texture heightMap) {
        super(camera, gameObjects, heightMap);

        this.prop = new TerrainToken();
        this.prop.transform = new Matrix4x4();
        this.prop.model = getModel(currentType);
    }

    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        prop.transform.setTranslation(screen.x, screen.y, 0);

        if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
            prop.transform.rotateLocalAxisZ(Input.mouse.getYDelta());
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            TerrainToken token = new TerrainToken();
            token.model = prop.model;
            token.transform = prop.transform.cpy();
            // adjustments
            if (currentType == PropType.TRANSMISSION_TOWER_LINES) {
                token.transform.translateLocalAxis(0,0,10.002f);
            }
            Vector2 xy = new Vector2(token.transform.getTranslationX(), token.transform.getTranslationY());
            float z = getHeight(xy.x, xy.y);
            token.transform.translateGlobalAxisXYZ(0, 0, z);

            gameObjects.add(token);
            currentIndex = allowedIndices[MathUtils.randomUniformInt(0, allowedIndices.length)];//MathUtils.randomUniformInt(0, 9);
            prop.model = getModel(currentType);
            writeData(token, PropType.class.getSimpleName(), currentType.prefix);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentIndex = 0;
            int nextOrdinal = (currentType.ordinal() + 1) % PropType.values().length;
            currentType = PropType.values()[nextOrdinal];
            prop.model = getModel(currentType);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
            int index = (currentType.ordinal() - 1 + PropType.values().length) % PropType.values().length;
            currentType = PropType.values()[index];
            prop.model = getModel(currentType);
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < prop.model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(prop.model.meshes[i], prop.model.materials[i], prop.transform);
        }
    }

    public enum PropType {
        FENCE("assets/app-models/prop-fence.fbx"),
        SATELLITE_DISH_SMALL("assets/app-models/prop-satellite-dish-small.fbx"),
        SHIPPING_CONTAINER_1("assets/app-models/prop-shipping-container_1.fbx"),
        SHIPPING_CONTAINER_2("assets/app-models/prop-shipping-container_2.fbx"),
        SHIPPING_CONTAINER_3("assets/app-models/prop-shipping-container_3.fbx"),
        SOLAR_PANELS("assets/app-models/prop-solar-panels.fbx"),
        TRANSMISSION_TOWER("assets/app-models/prop-transmission-tower.fbx"),
        TRANSMISSION_TOWER_LINES("assets/app-models/prop-transmission-tower-lines.fbx"),
        WIND_TURBINE_TOWER("assets/app-models/prop-wind-turbine-fan.fbx"),
        WIND_TURBINE_FAN("assets/app-models/prop-wind-turbine-tower.fbx"),
        WINDMILL_1("assets/app-models/prop-windmill_1.fbx"),
        WINDMILL_2("assets/app-models/prop-windmill_2.fbx"),
        WHEAT_FIELD_1("assets/app-models/fields_1.fbx"),
        WHEAT_FIELD_2("assets/app-models/fields_2.fbx"),
        WHEAT_FIELD_3("assets/app-models/fields_3.fbx"),
        WHEAT_FIELD_4("assets/app-models/fields_4.fbx"),
        ;

        public final String prefix;

        PropType(final String prefix) {this.prefix = prefix;}
    }

    private static Model getModel(PropType type) {
        final String path = type.prefix;
        return Assets.get(path);
    }

}
