package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Shader;

import java.util.HashMap;

@Deprecated
public class AssetLoaderShader implements AssetLoader<Shader> {

    private String vertexShaderSrcCode;
    private String fragmentShaderSrcCode;

    @Override
    public Array<AssetDescriptor> backgroundLoad(String path, final HashMap<String, Object> options) {
        final String vertexShaderFilepath = (String) options.get("vertexShaderFilepath");
        final String fragmentShaderFilepath = (String) options.get("fragmentShaderFilepath");
        vertexShaderSrcCode = Assets.getFileContent(vertexShaderFilepath);
        fragmentShaderSrcCode = Assets.getFileContent(fragmentShaderFilepath);
        return null;
    }

    @Override
    public Shader create() {
        return new Shader(vertexShaderSrcCode, fragmentShaderSrcCode);
    }

}