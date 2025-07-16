package com.heavybox.jtix.zzz_project;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;

public class GameObjectTerrainWaterBlock extends GameObject {

    private Shader shader;
    private ModelMesh mesh;
    private ModelMaterial material;

    private int index_i;
    private int index_j;
    private float time = 0;

    public GameObjectTerrainWaterBlock(int index_i, int index_j) {
        this.index_i = index_i;
        this.index_j = index_j;
        transform.translateGlobalAxisXYZ(-3500 + index_j * 1000,3500 - index_i * 1000,20);

        // load everything if not loaded
        // load water shader
        Assets.loadShader("water-shader", "assets/game-shaders/water.vert","assets/game-shaders/water.frag");
        // load model
        Assets.loadModel("assets/game-maps/terrain-tile-257.fbx");
        Assets.finishLoading();

        shader = Assets.get("water-shader");


        Model waterModel = Assets.get("assets/game-maps/terrain-tile-257.fbx");
        mesh = waterModel.meshes[0];

        material = waterModel.materials[0].clone();
        material.materialAttributes.put("time", 0.0f);
        material.materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        material.materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        material.materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        material.materialAttributes.put("uWavesAmplitude", 1.6f);
        material.materialAttributes.put("uWavesSpeed", 0.1f);
        material.materialAttributes.put("uWavesFrequency", 0.002f);
        material.materialAttributes.put("uWavesPersistence", 1);
        material.materialAttributes.put("uWavesLacunarity", 2.4f);
        material.materialAttributes.put("uWavesIterations", 3);
        material.materialAttributes.put("uTroughThreshold", 0f);
        material.materialAttributes.put("uTroughTransition", 8f);
        material.materialAttributes.put("uPeakThreshold", 22);
        material.materialAttributes.put("uPeakTransition", 0.1f);
        material.transparent = false;
    }

    public void update(float delta) {
        time += 1 * delta;
        material.materialAttributes.put("time", time % 600);
    }

    public void render() {
        Renderer3D.drawModel(shader, mesh, material, transform);
    }

}
