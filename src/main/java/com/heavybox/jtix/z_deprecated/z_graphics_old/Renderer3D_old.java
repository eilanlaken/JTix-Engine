package com.heavybox.jtix.z_deprecated.z_graphics_old;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_deprecated.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_deprecated.z_ecs_old.ComponentTransform_1;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO: improve to handle whatever.
@Deprecated public class Renderer3D_old {

    private boolean drawing;
    private Shader currentShader;
    private ComponentGraphicsCamera componentGraphicsCamera;

    public Renderer3D_old() {
        this.drawing = false;
    }

    public void begin(Shader shader) {
        this.currentShader = shader;
        ShaderBinder.bind(shader);
    }

    public void setCamera(final ComponentGraphicsCamera componentGraphicsCamera) {
        this.currentShader.bindUniform("u_camera_position", componentGraphicsCamera.position);
        this.currentShader.bindUniform("u_camera_combined", componentGraphicsCamera.lens.combined);
        this.componentGraphicsCamera = componentGraphicsCamera;
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setEnvironment() {
        // bind all lights.
        // ambient light
        //this.currentShader.bindUniform("ambient", environment.getTotalAmbient());

    }

    public void draw(final ModelPart modelPart, final ComponentTransform_1 transform) {
        // TODO: maybe updating the bounding sphere should be somewhere else.
        float centerX = modelPart.mesh.boundingSphereCenter.x;
        float centerY = modelPart.mesh.boundingSphereCenter.y;
        float centerZ = modelPart.mesh.boundingSphereCenter.z;
        Vector3 boundingSphereCenter = new Vector3(centerX + transform.x, centerY + transform.y, centerZ + transform.z);
        float boundingSphereRadius = MathUtils.max(transform.scaleX, transform.scaleY, transform.scaleZ) * modelPart.mesh.boundingSphereRadius;
        if (componentGraphicsCamera.lens.frustumIntersectsSphere(boundingSphereCenter, boundingSphereRadius)) {
            System.out.println("intersects");
        } else {
            System.out.println("CULLING");
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // todo: see when it makes sense to compute the matrix transform
        currentShader.bindUniform("u_body_transform", transform.world());
        ModelPartMaterial material = modelPart.material;
        //currentShader.bindUniforms(material.materialParams);
        currentShader.bindUniform("colorDiffuse", material.uniformParams.get("colorDiffuse"));
        ModelPartMesh mesh = modelPart.mesh;
        System.out.println("ddddd " + mesh.vaoId);
        GL30.glBindVertexArray(mesh.vaoId);
        {
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) {
                System.out.println("attrib: " + attribute.slot);
                if (mesh.hasVertexAttribute(attribute)) GL20.glEnableVertexAttribArray(attribute.slot);
            }
            if (mesh.indexed) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.slot);
        }
        GL30.glBindVertexArray(0);
    }

    public void end() {
        //ShaderProgramBinder.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
