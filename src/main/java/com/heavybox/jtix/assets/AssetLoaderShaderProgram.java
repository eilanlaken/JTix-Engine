package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.ShaderProgram;

public class AssetLoaderShaderProgram implements AssetLoader<ShaderProgram> {

    private String vertexShaderSrc;
    private String fragmentShaderSrc;

    @Override
    public void asyncLoad(String path) {
        vertexShaderSrc = AssetUtils.getFileContent(path + ".vert");
        fragmentShaderSrc = AssetUtils.getFileContent(path + ".frag");
    }

    @Override
    public ShaderProgram create() {
        return new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

}
