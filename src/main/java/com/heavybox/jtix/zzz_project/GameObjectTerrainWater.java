package com.heavybox.jtix.zzz_project;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;

// infinite water plane:
// https://asliceofrendering.com/scene%20helper/2020/01/05/InfiniteGrid/
public class GameObjectTerrainWater extends GameObject {

    public Shader shader;
    public ModelMesh mesh;
    public ModelMaterial material;
    public Scene3D scene;

    private float time = 0;

    public GameObjectTerrainWater(int index_i, int index_j) {
        // load everything if not loaded
        // load water shader
        Assets.loadShader("water-shader", "assets/game-shaders/water-2.vert","assets/game-shaders/water-2.frag");
        // load model
        Assets.loadScene("assets/game-maps/terrain-blocks-LODs-2km.fbx", "assets/game-textures");
        Assets.finishLoading();

        shader = Assets.get("water-shader");

        scene = Assets.get("assets/game-maps/terrain-blocks-LODs-2km.fbx");
        Scene3D.Node node;
        node = scene.namedNodes.get("square");
        node = scene.namedNodes.get("LOD_0");
        mesh = node.model.meshes[0];

        material = node.model.materials[0].clone();
        material.materialAttributes.put("time", 0.0f);
        material.materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        material.materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        material.materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        material.materialAttributes.put("uWavesAmplitude", 2.0f);
        material.materialAttributes.put("uWavesSpeed", 0.4f);
        material.materialAttributes.put("uWavesFrequency", 0.002f);
        material.materialAttributes.put("uWavesPersistence", 5);
        material.materialAttributes.put("uWavesLacunarity", 2.4f);
        material.materialAttributes.put("uWavesIterations", 3);
        material.materialAttributes.put("uTroughThreshold", 0f);
        material.materialAttributes.put("uTroughTransition", 8f);
        material.materialAttributes.put("uPeakThreshold", 22);
        material.materialAttributes.put("uPeakTransition", 0.1f);
//        material.materialAttributes.put("uPeakThreshold", 8); // TODO: this will make the colors show
//        material.materialAttributes.put("uPeakTransition", 3f);
        material.transparent = false;
    }

    public GameObjectTerrainWater(int index_i, int index_j, float x, float y) {
        transform.translateGlobalAxisXYZ(x, y, 0);
        // load everything if not loaded
        // load water shader
        Assets.loadShader("water-shader", "assets/game-shaders/water-2.vert","assets/game-shaders/water-2.frag");
        // load model
        Assets.loadScene("assets/game-maps/terrain-blocks-2km.fbx", "assets/game-textures");
        Assets.finishLoading();

        shader = Assets.get("water-shader");

        scene = Assets.get("assets/game-maps/terrain-blocks-2km.fbx");
        Scene3D.Node node;
        node = scene.namedNodes.get("LOD-0");
        node = scene.namedNodes.get("water");
        node = scene.namedNodes.get("square");
        mesh = node.model.meshes[0];

        material = node.model.materials[0].clone();
        material.materialAttributes.put("time", 0.0f);
        material.materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        material.materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        material.materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        material.materialAttributes.put("uWavesAmplitude", 2.1f);
        material.materialAttributes.put("uWavesSpeed", 0.2f);
        material.materialAttributes.put("uWavesFrequency", 0.002f);
        material.materialAttributes.put("uWavesPersistence", 1);
        material.materialAttributes.put("uWavesLacunarity", 4.4f);
        material.materialAttributes.put("uWavesIterations", 2);
        material.materialAttributes.put("uTroughThreshold", 0f);
        material.materialAttributes.put("uTroughTransition", 8f);
        material.materialAttributes.put("uPeakThreshold", 22);
        material.materialAttributes.put("uPeakTransition", 0.1f);
        material.transparent = false;
    }

    public void update(float delta) {
        time += 1 * delta;
        material.materialAttributes.put("time", time % 600); // TODO: use mod(u_time, 600.0) in glsl
    }

    public void render() {
        Renderer3D.drawModel(shader, mesh, material, transform);
    }

}
