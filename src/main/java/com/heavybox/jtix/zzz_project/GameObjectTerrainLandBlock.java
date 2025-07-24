package com.heavybox.jtix.zzz_project;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;

@Deprecated
public class GameObjectTerrainLandBlock extends GameObject {

    private Shader shader;
    private ModelMesh mesh;
    private ModelMaterial material;
    private int index_i, index_j;


    public GameObjectTerrainLandBlock(int index_i, int index_j) {
        super();
        this.index_i = index_i;
        this.index_j = index_j;

        transform.translateGlobalAxisXYZ(-3500 + index_j * 1000,3500 - index_i * 1000,0);

        // load everything if not loaded
        Assets.loadShader("terrain-shader", "assets/game-shaders/terrain-4.vert","assets/game-shaders/terrain-4.frag");

        Assets.loadModel("assets/game-maps/terrain-tile-257.fbx");
        Assets.loadTexture("assets/game-maps/terrain-blendmap-2048.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/heightmap-sample-2.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-stone.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-grass-dark.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-road.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-water.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-new-bright.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-new.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();

        shader = Assets.get("terrain-shader");
        Texture terrainBlendMap = Assets.get("assets/game-maps/terrain-blendmap-2048.png");
        Texture terrainHeightMap = Assets.get("assets/game-maps/heightmap-sample-2.jpg");
        Texture terrainStone = Assets.get("assets/game-maps/terrain-stone.jpg");
        Texture terrainGrass = Assets.get("assets/game-maps/terrain-grass-dark.jpg"); // empty
        Texture terrainRoad = Assets.get("assets/game-maps/terrain-road.jpg"); // r
        Texture terrainWheatBright = Assets.get("assets/game-maps/terrain-wheat-new-bright.jpg"); // g
        Texture terrainWheatDark = Assets.get("assets/game-maps/terrain-wheat-new.jpg"); // b
        Texture terrainWater = Assets.get("assets/game-maps/terrain-water.jpg"); // a

        Model model = Assets.get("assets/game-maps/terrain-tile-257.fbx");

        mesh = model.meshes[0];

        material = model.materials[0].clone();
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
    public void render() {
        Renderer3D.drawModel(shader, mesh, material, transform);
    }

}
