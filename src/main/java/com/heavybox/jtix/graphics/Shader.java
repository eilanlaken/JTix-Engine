package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.MapObjectInt;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.math.Vector4;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader implements MemoryResource {

    public final String vertexShaderSource;
    public final String fragmentShaderSource;
    public final int    program;
    public final int    vertexShaderId;
    public final int    fragmentShaderId;
    public final int    vertexAttributesBitmask;

    public final MapObjectInt<String> attributeLocations;
    public final MapObjectInt<String> attributeTypes;
    public final MapObjectInt<String> attributeSizes;
    public final MapObjectInt<String> uniformLocations;
    public final MapObjectInt<String> uniformTypes;
    public final MapObjectInt<String> uniformSizes;
    public final String[]             attributeNames;
    public final String[]             uniformNames;


    private final Object[] uniformCache; // TODO: remove
    private final Object[] uniformsCache; // TODO: use this

    public Shader(final String vertexShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null)   throw new IllegalArgumentException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new IllegalArgumentException("Fragment shader cannot be null.");

        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
        // attributes
        this.attributeLocations = new MapObjectInt<>();
        this.attributeTypes = new MapObjectInt<>();
        this.attributeSizes = new MapObjectInt<>();
        // uniforms
        this.uniformLocations = new MapObjectInt<>();
        this.uniformTypes = new MapObjectInt<>();
        this.uniformSizes = new MapObjectInt<>();
        this.program = GL20.glCreateProgram();
        if (program == 0) throw new RuntimeException("Could not create shader");

        /* create vertex shader */
        this.vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        if (vertexShaderId == 0)
            throw new RuntimeException("Error creating vertex shader.");
        GL20.glShaderSource(vertexShaderId, vertexShaderSource);
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling vertex shader: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        GL20.glAttachShader(program, vertexShaderId);
        // this.vertexShaderId = createVertexShader(vertexShaderSource); // was

        /* create fragment shader */
        this.fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0)
            throw new RuntimeException("Error creating fragment shader.");
        GL20.glShaderSource(fragmentShaderId, fragmentShaderSource);
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling fragment shader: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        GL20.glAttachShader(program, fragmentShaderId);
        // this.fragmentShaderId = createFragmentShader(fragmentShaderSource); // was

        /* link program */
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) throw new RuntimeException("Error linking shader code: " + GL20.glGetProgramInfoLog(program, 1024));
        GL20.glDetachShader(program, vertexShaderId);
        GL20.glDetachShader(program, fragmentShaderId);
        GL20.glValidateProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == 0)
            throw new RuntimeException("Could not validate shader code: " + GL20.glGetProgramInfoLog(program, 1024));

        /* register attributes */
        IntBuffer params_attributes = BufferUtils.createIntBuffer(1);
        IntBuffer type_attributes = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_ATTRIBUTES, params_attributes);
        int numAttributes = params_attributes.get(0);
        this.attributeNames = new String[numAttributes];
        for(int i = 0; i < numAttributes; ++i) {
            params_attributes.clear();
            params_attributes.put(0, 1);
            type_attributes.clear();
            String name = GL20.glGetActiveAttrib(this.program, i, params_attributes, type_attributes);
            int location = GL20.glGetAttribLocation(this.program, name);
            this.attributeLocations.put(name, location);
            this.attributeTypes.put(name, type_attributes.get(0));
            this.attributeSizes.put(name, params_attributes.get(0));
            this.attributeNames[i] = name;
        }
        this.vertexAttributesBitmask = ShaderVertexAttribute.getShaderAttributeBitmask(attributeNames);

        /* register uniforms */
        IntBuffer params_uniforms = BufferUtils.createIntBuffer(1);
        IntBuffer type_uniforms = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_UNIFORMS, params_uniforms);
        int uniformSymbolsCount = params_uniforms.get(0);
        for (int i = 0; i < uniformSymbolsCount; i++) {
            params_uniforms.clear();
            params_uniforms.put(0, 1);
            type_uniforms.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params_uniforms, type_uniforms);
            int size = params_uniforms.get(0);
            final int location = GL20.glGetUniformLocation(this.program, name);
            this.uniformSizes.put(name, size);
            this.uniformTypes.put(name, type_uniforms.get(0));
            this.uniformLocations.put(name, location);
            if (size > 1) { // array of uniforms.
                String prefix = name.replaceAll("\\[.*?]", "");;
                for (int k = 1; k < size; k++) {
                    String nextName = prefix + "[" + k + "]";
                    this.uniformSizes.put(nextName, size);
                    this.uniformTypes.put(nextName, type_uniforms.get(0));
                    this.uniformLocations.put(nextName, location + k);
                }
            }
        }
        this.uniformNames = new String[uniformLocations.size];
        int i = 0;
        for (MapObjectInt.Entry<String> entry : uniformLocations) {
            this.uniformNames[i] = entry.key;
            i++;
        }

        /* instantiate cache */
        this.uniformCache = new Object[uniformNames.length]; // TODO: delete
        this.uniformsCache = new Object[uniformNames.length]; // TODO: use this instead

        /* validation */
        /* validate: limit the allowed max sampled textures */
        final int maxSampledTextures = GraphicsUtils.getMaxFragmentShaderTextureUnits();
        int sampledTextures = 0;
        for (MapObjectInt.Entry<String> uniform : uniformTypes.entries()) {
            int type = uniform.value;
            if (type == GL20.GL_SAMPLER_2D) sampledTextures++;
        }
        if (sampledTextures > maxSampledTextures) throw new IllegalArgumentException("Error: shader code trying " + "to sample " + sampledTextures + ". The allowed maximum on this hardware is " + maxSampledTextures);
        /* validate: attribute names should conform to ShaderVertexAttribute enum */
        boolean badAttributeName = false;
        for (final String attributeName : attributeNames) {
            boolean contained = false;
            for (ShaderVertexAttribute vertexAttribute : ShaderVertexAttribute.values()) {
                if (vertexAttribute.glslVariableName.equals(attributeName)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                badAttributeName = true;
                System.err.println("Warning: shader attribute " + attributeName + " is not " +
                        "a valid vertex attribute name.");
            }
        }
        if (badAttributeName) {
            System.err.println("Valid shader attribute names are: ");
            for (ShaderVertexAttribute shaderVertexAttribute : ShaderVertexAttribute.values()) {
                System.err.println(shaderVertexAttribute.glslVariableName);
            }
        }
    }

    protected final void bindUniforms(final HashMap<String, Object> uniforms) {
        if (uniforms == null) return;
        for (Map.Entry<String, Object> entry : uniforms.entrySet()) {
            final String name = entry.getKey();
            final Object value = uniforms.get(name);
            bindUniform(name, value);
        }
    }

    public void bindUniform(final String name, final Object value) {
        if (value == null) return;
        final int location = uniformLocations.get(name, -1);
        // TODO: remove. Good only for debugging, but prevents custom flexible shading.
        if (location == -1) throw new IllegalArgumentException("\n\nError: " + this.getClass().getSimpleName() +  " does not have a uniform named " + name + "." + "\nIf you have defined the uniform but have not used it, the GLSL compiler discarded it.\n");
        final int type = uniformTypes.get(name, -1);
        switch (type) {

            case GL20.GL_SAMPLER_2D:
                Texture texture = (Texture) value;
                int slot = TextureBinder.bind(texture);
                if (isUniformIntegerCached(location, slot)) return;
                GL20.glUniform1i(location, slot);
                cacheUniformInteger(location, slot);
                break;

            case GL20.GL_INT:
                int i = (Integer) value;
                if (isUniformIntegerCached(location, i)) return;
                GL20.glUniform1i(location, i);
                cacheUniformInteger(location, i);
                break;

            case GL20.GL_FLOAT:
                float f = (Float) value;
                if (isUniformFloatCached(location, f)) return;
                GL20.glUniform1f(location, f);
                cacheUniformFloat(location, f);
                break;

            case GL20.GL_FLOAT_MAT4:
                Matrix4x4 matrix4 = (Matrix4x4) value;
                if (isUniformMatrix4Cached(location, matrix4)) return;
                GL20.glUniformMatrix4fv(location, false, matrix4.val);
                cacheUniformMatrix4(location, matrix4);
                break;

            case GL20.GL_FLOAT_VEC3:
                Vector3 vector3 = (Vector3) value;
                if (isUniformFloatTupleCached(location, vector3.x, vector3.y, vector3.z)) return;
                GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z);
                cacheUniformFloatTuple(location, vector3.x, vector3.y, vector3.z);
                break;

            case GL20.GL_FLOAT_VEC4:
                if (value instanceof Color) {
                    Color color = (Color) value;
                    if (isUniformFloatTupleCached(location, color.r, color.g, color.b, color.a)) return;
                    GL20.glUniform4f(location, color.r, color.g, color.b, color.a);
                    cacheUniformFloatTuple(location, color.r, color.g, color.b, color.a);
                } else if (value instanceof Vector4) {
                    Vector4 vector4 = (Vector4) value;
                    if (isUniformFloatTupleCached(location, vector4.x, vector4.y, vector4.z, vector4.w)) return;
                    GL20.glUniform4f(location, vector4.x, vector4.y, vector4.z, vector4.w);
                    cacheUniformFloatTuple(location, vector4.x, vector4.y, vector4.z, vector4.w);
                } else if (value instanceof Quaternion) {
                    Quaternion quaternion = (Quaternion) value;
                    if (isUniformFloatTupleCached(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w)) return;
                    GL20.glUniform4f(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
                    cacheUniformFloatTuple(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
                }
                break;

        }
    }

    @Override
    public void delete() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(vertexShaderId);
        GL20.glDeleteProgram(fragmentShaderId);
        GL20.glDeleteProgram(program);
    }

    @Override
    public String toString() {
        return "Shader : " + program + '\n' +
                "Vertex Shader : " + '\n' + vertexShaderSource + '\n' +
                "Fragment Shader : " + '\n' + fragmentShaderSource + '\n' +
                "Attributes Bitmask : " + '\n' + Integer.toBinaryString(vertexAttributesBitmask) + '\n';
    }

    // TODO: remove these methods and classes.

    @Deprecated private boolean isUniformIntegerCached(final int location, int value) {
        final IntegerCache cached = (IntegerCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    @Deprecated private boolean isUniformBooleanCached(final int location, boolean value) {
        final BooleanCache cached = (BooleanCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    @Deprecated private boolean isUniformFloatCached(final int location, float value) {
        final FloatCache cached = (FloatCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    @Deprecated private boolean isUniformFloatTupleCached(final int location, float x, float y) {
        final FloatTuple2Cache cached = (FloatTuple2Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y;
    }

    @Deprecated private boolean isUniformFloatTupleCached(final int location, float x, float y, float z) {
        final FloatTuple3Cache cached = (FloatTuple3Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y && cached.z == z;
    }

    @Deprecated private boolean isUniformFloatTupleCached(final int location, float x, float y, float z, float w) {
        final FloatTuple4Cache cached = (FloatTuple4Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y && cached.z == z && cached.w == w;
    }

    @Deprecated private boolean isUniformMatrix4Cached(final int location, final Matrix4x4 value) {
        final Matrix4Cache cached = (Matrix4Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.value.equals(value);
    }

    @Deprecated private void cacheUniformInteger(final int location, final int value) {
        if (uniformCache[location] == null) uniformCache[location] = new IntegerCache();
        ((IntegerCache) uniformCache[location]).value = value;
    }

    @Deprecated private void cacheUniformBoolean(final int location, final boolean value) {
        if (uniformCache[location] == null) uniformCache[location] = new BooleanCache();
        ((BooleanCache) uniformCache[location]).value = value;
    }

    @Deprecated private void cacheUniformFloat(final int location, final float value) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatCache();
        ((FloatCache) uniformCache[location]).value = value;
    }

    @Deprecated private void cacheUniformFloatTuple(final int location, float x, float y) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple2Cache();
        FloatTuple2Cache cache = (FloatTuple2Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
    }

    @Deprecated private void cacheUniformFloatTuple(final int location, float x, float y, float z) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple3Cache();
        FloatTuple3Cache cache = (FloatTuple3Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
        cache.z = z;
    }

    @Deprecated private void cacheUniformFloatTuple(final int location, float x, float y, float z, float w) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple4Cache();
        FloatTuple4Cache cache = (FloatTuple4Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
        cache.z = z;
        cache.w = w;
    }

    @Deprecated private void cacheUniformMatrix4(final int location, final Matrix4x4 value) {
        if (uniformCache[location] == null) uniformCache[location] = new Matrix4Cache();
        Matrix4Cache cache = (Matrix4Cache) uniformCache[location];
        cache.value.set(value);
    }

    @Deprecated private static class IntegerCache {
        private int value;
    }

    @Deprecated private static class BooleanCache {
        private boolean value;
    }

    @Deprecated private static class FloatCache {
        private float value;
    }

    @Deprecated private static class FloatTuple2Cache {
        private float x;
        private float y;
    }

    @Deprecated private static class FloatTuple3Cache {
        private float x;
        private float y;
        private float z;
    }

    @Deprecated private static class FloatTuple4Cache {
        private float x;
        private float y;
        private float z;
        private float w;
    }

    @Deprecated private static class Matrix4Cache {
        private final Matrix4x4 value = new Matrix4x4();
    }

}
