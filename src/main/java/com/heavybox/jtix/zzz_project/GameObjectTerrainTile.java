package com.heavybox.jtix.zzz_project;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;

public class GameObjectTerrainTile extends GameObject {

    private Shader shader;
    private ModelMesh meshLOD0;
    private ModelMaterial material;
    private Scene3D scene;


    public GameObjectTerrainTile(int index_i, int index_j) {
        super();



        // terrain shader
        Assets.loadShader("terrain-shader", "assets/game-shaders/terrain-5.vert","assets/game-shaders/terrain-5.frag");
        // terrain models
        Assets.loadScene("assets/game-maps/terrain-blocks-2km.fbx", "assets/game-textures");
        // terrain topology
        Assets.loadTexture("assets/game-maps/terrain-blendmap-2048.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/heightmap-128.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());

        Assets.loadTexture("assets/game-maps/blendmap-8k.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/heightmap-8k.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());

        // terrain colors
        Assets.loadTexture("assets/game-maps/terrain-stone.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-grass-dark.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-road.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-water.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-new-bright.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-new.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        // wait
        Assets.finishLoading();

        shader = Assets.get("terrain-shader");
        //Texture terrainBlendMap = Graphics.getTextureSingleBlackPixelOpaque();
        Texture terrainBlendMap = Assets.get("assets/game-maps/blendmap-8k.jpg");
        Texture terrainHeightMap = Assets.get("assets/game-maps/heightmap-8k.jpg");

        Texture terrainStone = Assets.get("assets/game-maps/terrain-stone.jpg");
        Texture terrainGrass = Assets.get("assets/game-maps/terrain-grass-dark.jpg"); // empty
        Texture terrainRoad = Assets.get("assets/game-maps/terrain-road.jpg"); // r
        Texture terrainWheatBright = Assets.get("assets/game-maps/terrain-wheat-new-bright.jpg"); // g
        Texture terrainWheatDark = Assets.get("assets/game-maps/terrain-wheat-new.jpg"); // b
        Texture terrainWater = Assets.get("assets/game-maps/terrain-water.jpg"); // a

        scene = Assets.get("assets/game-maps/terrain-blocks-2km.fbx");
        Scene3D.Node node = scene.namedNodes.get("LOD-0");
        //Scene3D.Node node = scene.namedNodes.get("water");
        meshLOD0 = node.model.meshes[0];

        material = node.model.materials[0].clone();
        material.materialAttributes.put("u_texture_steep", terrainStone);
        material.materialAttributes.put("u_texture_background", terrainGrass);
        material.materialAttributes.put("u_texture_red", terrainRoad);
        material.materialAttributes.put("u_texture_green", terrainWheatDark);
        material.materialAttributes.put("u_texture_blue", terrainWheatBright);
        material.materialAttributes.put("u_texture_alpha", terrainWater);
        material.materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        material.materialAttributes.put("u_texture_height_map", terrainHeightMap);
        material.materialAttributes.put("u_tile_index_row", index_i);
        material.materialAttributes.put("u_tile_index_col", index_j);
        material.transparent = false;


        // offset transform
    }

    @Override
    public void update(float delta) {
        // switch lod
    }

    @Override
    public void render() {
        Renderer3D.drawModel(shader, meshLOD0, material, transform);
    }

}
