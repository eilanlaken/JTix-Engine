package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.Matrix4x4;

public class TerrainTile {

    public Matrix4x4 transform = new Matrix4x4();

    public Model model;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;

    public Array<TerrainToken> staticGameObjects = new Array<>(false, 5); // trees, houses, buildings, roads, props.

    public TerrainTile(int i, int j, Texture heightmap, Texture blendmap) {
        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.frag");
        this.terrainShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");

        this.terrainHeightMap = heightmap;
        this.terrainBlendMap = blendmap;

        model = Assets.get("assets/app-models/terrain-block.fbx");
        model.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        model.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        model.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        model.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        model.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        model.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);

        // set terrain transform based on i,j
    }

    private void buildStaticTile() {

    }

}
