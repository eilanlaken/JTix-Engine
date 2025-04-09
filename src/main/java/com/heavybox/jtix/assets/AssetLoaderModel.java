package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.MapObjectInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class AssetLoaderModel implements AssetLoader<Model> {

    private static final MapObjectInt<String> uniformNameTextureTypes = new MapObjectInt<>();
    static {
        uniformNameTextureTypes.put("u_baseColor", Assimp.aiTextureType_BASE_COLOR);
        uniformNameTextureTypes.put("u_diffuse", Assimp.aiTextureType_DIFFUSE);
    }

    private MeshData[] meshesData;
    private MaterialData[] materialsData;
    private String folderPath;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        this.folderPath = Paths.get(path).getParent().toString();
        System.out.println(folderPath);
        // TODO: use the options here.
        final int importFlags =
                Assimp.aiProcess_Triangulate |
                Assimp.aiProcess_ImproveCacheLocality |
                Assimp.aiProcess_GenBoundingBoxes |
                Assimp.aiProcess_CalcTangentSpace |
                Assimp.aiProcess_RemoveRedundantMaterials
                ;

        AIScene aiScene = Assimp.aiImportFile(path, importFlags);

        // load meshes:
        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();
        meshesData = new MeshData[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            final MeshData meshData = processMesh(aiMesh);
            meshesData[i] = meshData;
        }

        // load materials
        PointerBuffer aiMaterials  = aiScene.mMaterials();
        int numMaterials = aiScene.mNumMaterials();
        materialsData = new MaterialData[numMaterials];
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            final MaterialData materialData = processMaterial(aiMaterial);
            materialsData[i] = materialData;
        }

        Array<AssetDescriptor> dependencies = new Array<>();
        // load textures as materials.
        for (MaterialData materialData : materialsData) {
            Array<MaterialTextureData> texturesData = materialData.texturesData;
            for (MaterialTextureData textureData : texturesData) {
                AssetDescriptor assetDescriptor = new AssetDescriptor(Texture.class, textureData.path, null); // TODO options
                dependencies.add(assetDescriptor);
            }
        }

        return dependencies;
    }

    @Override
    public Model afterLoad() {
        ModelMesh[] modelMeshes = new ModelMesh[meshesData.length];
        for (int i = 0; i < modelMeshes.length; i++) {
            MeshData meshData = meshesData[i];
            modelMeshes[i] = new ModelMesh(meshData.positions, meshData.textureCoords0, meshData.colors, meshData.normals, meshData.indices, meshData.boundingSphereRadius);
        }

        ModelMaterial[] modelMaterials = new ModelMaterial[materialsData.length];
        for (int i = 0; i < modelMaterials.length; i++) {
            MaterialData materialData = materialsData[i];
            ModelMaterial modelMaterial = new ModelMaterial();
            // add all the textures
            for (MaterialTextureData materialTextureData : materialData.texturesData) {
                Texture texture = Assets.get(materialTextureData.path);
                modelMaterial.materialAttributes.put(materialTextureData.uniform, texture);
            }
            // TODO: add all the colors

            // TODO: add all the props (metallic, roughness etc.).

            modelMaterials[i] = modelMaterial;
        }

        System.out.println("lenght: " + modelMaterials.length);
        return new Model(modelMeshes, modelMaterials);
    }

    private MaterialData processMaterial(final AIMaterial aiMaterial) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            MaterialData materialData = new MaterialData();

            AIString ai_name = AIString.calloc();
            if (Assimp.aiGetMaterialString(aiMaterial, Assimp.AI_MATKEY_NAME, 0, 0, ai_name) == Assimp.aiReturn_SUCCESS) {
                materialData.name = ai_name.dataString();;
            }

            for (MapObjectInt.Entry<String> entry : uniformNameTextureTypes) {

                AIString ai_path = AIString.calloc();
                IntBuffer ai_mapping = stack.mallocInt(1);
                IntBuffer ai_uvIndex = stack.mallocInt(1);
                IntBuffer ai_op = stack.mallocInt(1);
                IntBuffer ai_mapMode = stack.mallocInt(1);
                FloatBuffer ai_blendMode = stack.mallocFloat(1);
                int result = Assimp.aiGetMaterialTexture(aiMaterial, entry.value, 0, ai_path, ai_mapping, ai_uvIndex, ai_blendMode, ai_op, ai_mapMode, null);
                if (result == Assimp.aiReturn_SUCCESS) {
                    MaterialTextureData materialTexture = new MaterialTextureData();
                    materialTexture.uniform = entry.key;
                    Path base = Paths.get(folderPath);
                    Path fullPath = base.resolve(ai_path.dataString());
                    materialTexture.path = fullPath.toString();
                    // ... TODO.

                    materialData.texturesData.add(materialTexture);
                }
            }

            return materialData;
        }
    }


    private MeshData processMesh(final AIMesh aiMesh) {
        MeshData meshData = new MeshData();
        meshData.positions = getPositions(aiMesh);
        meshData.colors = getColors(aiMesh);
        meshData.textureCoords0 = getTextureCoords0(aiMesh);
        meshData.normals = getNormals(aiMesh);
        meshData.indices = getIndices(aiMesh);
        meshData.vertexCount = getVertexCount(aiMesh);
        //meshData.boundingSphere = getBoundingSphere(aiMesh); TODO: remove this line
        // set bounding sphere radius
        AIAABB aiAABB = aiMesh.mAABB();
        AIVector3D min = aiAABB.mMin();
        AIVector3D max = aiAABB.mMax();
        Vector3 center = new Vector3();
        center.add(min.x(), min.y(), min.z());
        center.add(max.x(), max.y(), max.z());
        center.scl(0.5f);
        float radius = Vector3.dst(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
        meshData.boundingSphereRadius = radius + Vector3.len(center.x, center.y, center.z);
        return meshData;
    }

    private int getVertexCount(final AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        if (faceCount > 0) return faceCount * 3;
        else return aiMesh.mNumVertices();
    }

    private float[] getPositions(final AIMesh aiMesh) {
        AIVector3D.Buffer positionsBuffer = aiMesh.mVertices();
        float[] positions = new float[aiMesh.mVertices().limit() * 3];
        for (int i = 0; i < positionsBuffer.limit(); i++) {
            AIVector3D vector3D = positionsBuffer.get(i);
            positions[3*i] = vector3D.x();
            positions[3*i+1] = vector3D.y();
            positions[3*i+2] = vector3D.z();
        }
        return positions;
    }

    @Deprecated
    private float[] getColors_old(final AIMesh mesh) {
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        if (colorsBuffer == null) return null;
        float[] colors = new float[colorsBuffer.limit() * 4];
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[4*i] = color.r();
            colors[4*i+1] = color.g();
            colors[4*i+2] = color.b();
            colors[4*i+3] = color.a();
        }
        return colors;
    }

    private float[] getColors(final AIMesh mesh) {
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        if (colorsBuffer == null) return null;
        float[] colors = new float[colorsBuffer.limit()];
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[i] = Color.toFloatBits(color.r(), color.g(), color.b(), color.a());
        }
        return colors;
    }

    private float[] getTextureCoords0(final AIMesh mesh) {
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(0);
        if (textureCoordinatesBuffer == null) return null;
        float[] textureCoordinates0 = new float[mesh.mVertices().limit() * 2];
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates0[2*i] = coordinates.x();
            textureCoordinates0[2*i+1] = coordinates.y();
        }
        return textureCoordinates0;
    }

    // TODO: pack and normalize
    private float[] getNormals(final AIMesh mesh) {
        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        if (normalsBuffer == null) return null;
        float[] normals = new float[normalsBuffer.limit() * 3];
        for (int i = 0; i < normalsBuffer.limit(); i++) {
            AIVector3D vector3D = normalsBuffer.get(i);
            normals[3*i] = vector3D.x();
            normals[3*i+1] = vector3D.y();
            normals[3*i+2] = vector3D.z();
        }
        return normals;
    }

    private int[] getIndices(final AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        if (faceCount <= 0) return null;
        AIFace.Buffer faces = aiMesh.mFaces();
        int[] indices = new int[faceCount * 3];
        for (int i = 0; i < faceCount; i++) {
            AIFace aiFace = faces.get(i);
            if (aiFace.mNumIndices() != 3) throw new IllegalArgumentException("Faces were not properly triangulated");
            indices[3*i] = aiFace.mIndices().get(0);
            indices[3*i + 1] = aiFace.mIndices().get(1);
            indices[3*i + 2] = aiFace.mIndices().get(2);
        }

        return indices;
    }

    private static class MeshData {

        public int vertexCount;
        public float[] positions;
        public float[] colors;
        public float[] textureCoords0;
        public float[] normals;
        public int[] indices;
        public float   boundingSphereRadius;

    }

    private static class MaterialData {

        public String name;
        public Array<MaterialTextureData> texturesData = new Array<>();

    }

    private static class MaterialTextureData {

        public String uniform;
        public String path;
        public int uvIndex;
        public int mapping;
        public int mapMode;
        public int op;
        public float blendMode;

    }

}
