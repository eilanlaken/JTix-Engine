package com.heavybox.jtix.z_deprecated.z_old_assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.collections.MapObjectInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.math.Vector4;
import com.heavybox.jtix.memory.MemoryUtils;
import com.heavybox.jtix.z_deprecated.z_graphics_old.*;
import com.heavybox.jtix.z_deprecated.z_graphics_old.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO: recreate interleaved model building to minimize vbo count;
// TODO: move stuff to model builder;
@Deprecated public class AssetLoaderModel implements AssetLoader<Model> {

    private static final MapObjectInt<String> namedTextureTypes;
    private static final Map<String, String> namedColorParams;
    private static final Map<String, String> namedProps;
    static {
        namedTextureTypes = new MapObjectInt<>();
        namedTextureTypes.put("textureBaseColor", Assimp.aiTextureType_BASE_COLOR);
        namedTextureTypes.put("textureNormal", Assimp.aiTextureType_NORMALS);
        namedTextureTypes.put("textureDiffuse", Assimp.aiTextureType_DIFFUSE);
        namedTextureTypes.put("textureNone", Assimp.aiTextureType_NONE);
        namedTextureTypes.put("textureAmbient", Assimp.aiTextureType_AMBIENT);
        namedTextureTypes.put("textureAmbientOcclusion", Assimp.aiTextureType_AMBIENT_OCCLUSION);
        namedTextureTypes.put("textureClearCoat", Assimp.aiTextureType_CLEARCOAT);
        namedTextureTypes.put("textureDiffuseRoughness", Assimp.aiTextureType_DIFFUSE_ROUGHNESS);
        namedTextureTypes.put("textureDisplacement", Assimp.aiTextureType_DISPLACEMENT);
        namedTextureTypes.put("textureEmissionColor", Assimp.aiTextureType_EMISSION_COLOR);
        namedTextureTypes.put("textureEmissive", Assimp.aiTextureType_EMISSIVE);
        namedTextureTypes.put("textureHeight", Assimp.aiTextureType_HEIGHT);
        namedTextureTypes.put("textureLightmap", Assimp.aiTextureType_LIGHTMAP);
        namedTextureTypes.put("textureMetallic", Assimp.aiTextureType_METALNESS);
        namedTextureTypes.put("textureReflection", Assimp.aiTextureType_REFLECTION);
        namedTextureTypes.put("textureSpecular", Assimp.aiTextureType_SPECULAR);
        namedTextureTypes.put("textureShininess", Assimp.aiTextureType_SHININESS);
        namedTextureTypes.put("textureNormalCamera", Assimp.aiTextureType_NORMAL_CAMERA);
        namedTextureTypes.put("textureSheen", Assimp.aiTextureType_SHEEN);
        namedTextureTypes.put("textureOpacity", Assimp.aiTextureType_OPACITY);
        namedTextureTypes.put("textureTransmission", Assimp.aiTextureType_TRANSMISSION);
        namedTextureTypes.put("textureUnknown", Assimp.aiTextureType_UNKNOWN);

        namedColorParams = new HashMap<>();
        namedColorParams.put("colorAmbient", Assimp.AI_MATKEY_COLOR_AMBIENT);
        namedColorParams.put("colorDiffuse", Assimp.AI_MATKEY_COLOR_DIFFUSE);
        namedColorParams.put("colorEmissive", Assimp.AI_MATKEY_COLOR_EMISSIVE);
        namedColorParams.put("colorReflective", Assimp.AI_MATKEY_COLOR_REFLECTIVE);
        namedColorParams.put("colorSpecular", Assimp.AI_MATKEY_COLOR_SPECULAR);
        namedColorParams.put("colorTransparent", Assimp.AI_MATKEY_COLOR_TRANSPARENT);

        namedProps = new HashMap<>();
        namedProps.put("propAlpha", Assimp.AI_MATKEY_OPACITY);
        namedProps.put("propReflectivity", Assimp.AI_MATKEY_REFLECTIVITY);
        namedProps.put("propMetallic", Assimp.AI_MATKEY_METALLIC_FACTOR);
        namedProps.put("propTransparency", Assimp.AI_MATKEY_TRANSPARENCYFACTOR);
        namedProps.put("propShininess", Assimp.AI_MATKEY_SHININESS);
        namedProps.put("propShadingModel", Assimp.AI_MATKEY_SHADING_MODEL);
        namedProps.put("propRoughness", Assimp.AI_MATKEY_ROUGHNESS_FACTOR);
        namedProps.put("propTwoSided", Assimp.AI_MATKEY_TWOSIDED);
        namedProps.put("propGlossiness", Assimp.AI_MATKEY_GLOSSINESS_FACTOR);
    }

    private ModelPartMaterialData[] materialsData;
    private ModelPartMeshData[] meshesData;
    private ModelPartData[] partsData;
    private ModelArmatureData armatureData;

    @Override
    public Array<AssetDescriptor> asyncLoad(String path, AssetLoader.Options options) {
        Options modelOptions = (Options) options;

        final int importFlags = // TODO: use the options here.
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_ImproveCacheLocality |
                        Assimp.aiProcess_GenBoundingBoxes |
                        Assimp.aiProcess_GenNormals |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_RemoveRedundantMaterials;


        try (AIScene aiScene = Assimp.aiImportFile(path, importFlags)) {
            PointerBuffer aiMaterials  = aiScene.mMaterials();
            int numMaterials = aiScene.mNumMaterials();
            materialsData = new ModelPartMaterialData[numMaterials];
            for (int i = 0; i < numMaterials; i++) {
                materialsData[i] = processMaterial(AIMaterial.create(aiMaterials.get(i)));;
            }

            PointerBuffer aiMeshes = aiScene.mMeshes();
            int numMeshes = aiScene.mNumMeshes();
            meshesData = new ModelPartMeshData[numMeshes];
            for (int i = 0; i < numMeshes; i++) {
                final ModelPartMeshData meshData = processMesh(AIMesh.create(aiMeshes.get(i)));
                meshesData[i] = meshData;
            }

            partsData = new ModelPartData[numMeshes];
            for (int i = 0; i < partsData.length; i++) {
                partsData[i] = new ModelPartData();
                partsData[i].meshData = meshesData[i];
                partsData[i].materialData = materialsData[meshesData[i].materialIndex];
            }

            armatureData = new ModelArmatureData();
        }

        Array<AssetDescriptor> dependencies = new Array<>();
        for (ModelPartMaterialData materialData : materialsData) {
            Map<String, Object> attributesData = materialData.attributesData;
            for (Map.Entry<String, Object> entry : attributesData.entrySet()) {
                AssetLoaderTexture.Options textureLoaderOptions = new AssetLoaderTexture.Options();
                textureLoaderOptions.anisotropy = modelOptions.anisotropy;
//                textureLoaderOptions.magFilter = modelOptions.magFilter;
//                textureLoaderOptions.minFilter = modelOptions.minFilter;
                textureLoaderOptions.uWrap = modelOptions.uWrap;
                textureLoaderOptions.vWrap = modelOptions.vWrap;

                if (entry instanceof TextureParameters) {
                    dependencies.add(new AssetDescriptor(Texture.class, ((TextureParameters) entry).path, textureLoaderOptions));
                }
            }
        }
        return dependencies;

    }

    // TODO: use ModelBuilder for this part.
    // TODO: use interleaved vertices.
    @Override
    public Model create() {
        ModelPart[] parts = new ModelPart[partsData.length];
        for (int i = 0; i < parts.length; i++) {
            // create material
            ModelPartData partData = partsData[i];
            ModelPartMaterialData materialData = partData.materialData;
            ModelPartMeshData meshData = partData.meshData;
            HashMap<String, Object> materialAttributes = new HashMap<>();
            for (Map.Entry<String, Object> materialDataEntry : materialData.attributesData.entrySet()) {
                final String uniform = materialDataEntry.getKey();
                final Object dataValue = materialDataEntry.getValue();
                if (dataValue instanceof Color) {
                    Color color = (Color) dataValue;
                    materialAttributes.put(uniform, new Color(color.r, color.g, color.b, color.a));
                } else if (dataValue instanceof TextureParameters) {
                    TextureParameters params = (TextureParameters) dataValue;
                    Texture texture = AssetStore.get(params.path);
                    materialAttributes.put(uniform, texture);
                } else if (dataValue instanceof Float) {
                    materialAttributes.put(uniform, dataValue);
                }
            }
            ModelPartMaterial material = new ModelPartMaterial(materialAttributes);
            ModelPartMesh mesh = create(meshData);
            parts[i] = new ModelPart(mesh, material, null);
        }
        final ModelArmature armature = new ModelArmature();
        return new Model(parts, armature);
    }

    private ModelPartMaterialData processMaterial(final AIMaterial aiMaterial) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ModelPartMaterialData modelPartMaterialData = new ModelPartMaterialData();

            AIString name = AIString.calloc();
            if (Assimp.aiGetMaterialString(aiMaterial, Assimp.AI_MATKEY_NAME, 0, 0, name) == Assimp.aiReturn_SUCCESS) {
                modelPartMaterialData.name = name.dataString();
            }

            for (MapObjectInt.Entry<String> entry : namedTextureTypes) {
                final String uniform = entry.key;
                AIString path = AIString.calloc();
                IntBuffer mapping = stack.mallocInt(1);
                IntBuffer uvIndex = stack.mallocInt(1);
                IntBuffer op = stack.mallocInt(1);
                IntBuffer mapMode = stack.mallocInt(1);
                FloatBuffer blendMode = stack.mallocFloat(1);
                int result = Assimp.aiGetMaterialTexture(aiMaterial, entry.value, 0, path, mapping, uvIndex, blendMode, op, mapMode, null);
                if (result == Assimp.aiReturn_SUCCESS) {
                    TextureParameters params = new TextureParameters();
                    params.path = path.dataString();
                    params.mapMode = mapMode.get(0);
                    params.op = op.get(0);
                    params.blendMode = blendMode.get(0);
                    params.uvIndex = uvIndex.get(0);
                    params.mapping = mapping.get(0);
                    modelPartMaterialData.attributesData.put(uniform, params);
                }
            }

            AIColor4D colour = AIColor4D.create();
            for (Map.Entry<String, String> colorEntry : namedColorParams.entrySet()) {
                int result = Assimp.aiGetMaterialColor(aiMaterial, colorEntry.getValue(), Assimp.aiTextureType_NONE, 0, colour);
                if (result == Assimp.aiReturn_SUCCESS) {
                    modelPartMaterialData.attributesData.put(colorEntry.getKey(), new Color(colour.r(), colour.g(), colour.b(), colour.a()));
                }
            }

            PointerBuffer pointerBuffer = stack.mallocPointer(1);
            for (Map.Entry<String, String> namedProp : namedProps.entrySet()) {
                int result = Assimp.aiGetMaterialProperty(aiMaterial, namedProp.getValue(), pointerBuffer);
                if (result == Assimp.aiReturn_SUCCESS) {
                    AIMaterialProperty property = AIMaterialProperty.create(pointerBuffer.get(0));
                    int dataSize = property.mDataLength();
                    try {
                        if (dataSize == 4) modelPartMaterialData.attributesData.put(namedProp.getKey(), property.mData().asFloatBuffer().get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return modelPartMaterialData;
        }
    }

    private ModelPartMeshData processMesh(final AIMesh aiMesh) {
        ModelPartMeshData meshData = new ModelPartMeshData();
        meshData.vertexBuffers.put(VertexAttribute_old.POSITION_2D, getPositions(aiMesh));
        meshData.vertexBuffers.put(VertexAttribute_old.COLOR, getColors(aiMesh)); // TODO: change to color packed.
        meshData.vertexBuffers.put(VertexAttribute_old.TEXTURE_COORDINATES0, getTextureCoords0(aiMesh));
        meshData.vertexBuffers.put(VertexAttribute_old.TEXTURE_COORDINATES1, getTextureCoords1(aiMesh));
        meshData.vertexBuffers.put(VertexAttribute_old.NORMAL, getNormals(aiMesh));
        meshData.vertexBuffers.put(VertexAttribute_old.TANGENT, getTangents(aiMesh));
        meshData.vertexBuffers.put(VertexAttribute_old.BI_NORMAL, getBiNormals(aiMesh));
        meshData.indices = getIndices(aiMesh);
        meshData.materialIndex = aiMesh.mMaterialIndex();
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
        meshData.boundingSphereCenter = center;
        meshData.boundingSphereRadius = radius;
        return meshData;
    }

    // TODO: implement
    private Vector4 getBoundingAABB(final AIMesh aiMesh) {
//        AIAABB aiAABB = aiMesh.mAABB();
//        AIVector3D min = aiAABB.mMin();
//        AIVector3D max = aiAABB.mMax();
//
//        Vector3 center = new Vector3();
//        center.add(min.x(), min.y(), min.z());
//        center.add(max.x(), max.y(), max.z());
//        center.scl(0.5f);
//        float radius = Vector3.dst(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
//        return new Vector4(center, radius);
        return null;
    }

    // TODO: implement
    private Vector4 getBoundingSphere(final AIMesh aiMesh) {
//        AIAABB aiAABB = aiMesh.mAABB();
//        AIVector3D min = aiAABB.mMin();
//        AIVector3D max = aiAABB.mMax();
//        Vector3 center = new Vector3();
//        center.add(min.x(), min.y(), min.z());
//        center.add(max.x(), max.y(), max.z());
//        center.scl(0.5f);
//        float radius = Vector3.dst(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
//        return new Shape3DSphere(center, radius);
        return null;
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

    private float[] getColors(final AIMesh mesh) {
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

    private float[] getTextureCoords1(final AIMesh mesh) {
        AIVector3D.Buffer textureCoords1Buffer = mesh.mTextureCoords(1);
        if (textureCoords1Buffer == null) return null;
        float[] textureCoordinates1 = new float[mesh.mVertices().limit() * 2];
        for (int i = 0; i < textureCoords1Buffer.limit(); i++) {
            AIVector3D coordinates = textureCoords1Buffer.get(i);
            textureCoordinates1[2*i] = coordinates.x();
            textureCoordinates1[2*i+1] = coordinates.y();
        }
        return textureCoordinates1;
    }

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

    private float[] getTangents(final AIMesh mesh) {
        AIVector3D.Buffer tangentsBuffer = mesh.mTangents();
        if (tangentsBuffer == null) return null;
        float[] tangents = new float[tangentsBuffer.limit() * 3];
        for (int i = 0; i < tangentsBuffer.limit(); i++) {
            AIVector3D vector3D = tangentsBuffer.get(i);
            tangents[3*i] = vector3D.x();
            tangents[3*i+1] = vector3D.y();
            tangents[3*i+2] = vector3D.z();
        }
        return tangents;
    }

    private float[] getBiNormals(final AIMesh mesh) {
        AIVector3D.Buffer biNormalsBuffer = mesh.mBitangents();
        if (biNormalsBuffer == null) return null;
        float[] biNormals = new float[biNormalsBuffer.limit() * 3];
        for (int i = 0; i < biNormalsBuffer.limit(); i++) {
            AIVector3D vector3D = biNormalsBuffer.get(i);
            biNormals[3*i] = vector3D.x();
            biNormals[3*i+1] = vector3D.y();
            biNormals[3*i+2] = vector3D.z();
        }
        return biNormals;
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

    private ModelPartMesh create(final ModelPartMeshData meshData) {
        Array<VertexAttribute_old> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
            storeIndicesBuffer(meshData.indices, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.POSITION_2D, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.COLOR, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.TEXTURE_COORDINATES0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.TEXTURE_COORDINATES1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.TANGENT, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BI_NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT2, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT3, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT4, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute_old.BONE_WEIGHT5, meshData, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);
        final short bitmask = generateBitmask(attributesCollector);
        final int[] vbos = vbosCollector.pack();
        return new ModelPartMesh(vaoId, meshData.vertexCount, bitmask,meshData.indices != null, meshData.boundingSphereCenter, meshData.boundingSphereRadius, vbos);
    }

    private void storeIndicesBuffer(int[] indices, ArrayInt vbosCollector) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbosCollector.add(vbo);
    }

    private void storeDataInAttributeList(final VertexAttribute_old attribute, final ModelPartMeshData meshData, Array<VertexAttribute_old> attributesCollector, ArrayInt vbosCollector) {
        final float[] data = (float[]) meshData.vertexBuffers.get(attribute);
        if (data == null) return;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.length;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        attributesCollector.add(attribute);
        vbosCollector.add(vbo);
    }

    private short generateBitmask(final Array<VertexAttribute_old> attributes) {
        short bitmask = 0b0000;
        for (final VertexAttribute_old attribute : attributes) {
            bitmask |= attribute.bitmask;
        }
        return bitmask;
    }

    private static class ModelPartMeshData {
        public int vertexCount;
        public Map<VertexAttribute_old, Object> vertexBuffers = new HashMap<>();
        public int materialIndex;
        public int[] indices;
        public Vector3 boundingSphereCenter;
        public float   boundingSphereRadius;
    }

    private static class ModelPartMaterialData {
        public String name;
        public Map<String, Object> attributesData = new HashMap<>();
    }

    private static class ModelPartData {
        public ModelPartMeshData meshData;
        public ModelPartMaterialData materialData;
    }

    private static class ModelArmatureData {

    }

    private static class ModelData {
        public ModelPartData[] partsData;
        public ModelArmatureData armatureData;

        ModelData(final ModelPartData[] partsData, final ModelArmatureData armatureData) {
            this.partsData = partsData;
            this.armatureData = armatureData;
        }

    }

    private static class TextureParameters {
        public String path;
        public int uvIndex;
        public int mapping;
        public int mapMode;
        public int op;
        public float blendMode;
    }

    public static final class Options extends AssetLoader.Options<Model> {

        // TODO
        public boolean        triangulate = false;
        public boolean        flipNormals = false;


        public int            anisotropy = Graphics.getMaxAnisotropy();
        public Texture.Wrap   uWrap      = Texture.Wrap.CLAMP_TO_EDGE;
        public Texture.Wrap   vWrap      = Texture.Wrap.CLAMP_TO_EDGE;

    }

}
