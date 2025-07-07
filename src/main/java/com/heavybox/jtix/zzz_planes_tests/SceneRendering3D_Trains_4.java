package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.graphics.Renderer3D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Trains_4 implements Scene {

    private Camera camera;

    public Model model_train;
    public Matrix4x4 transform_train = new Matrix4x4();
    public Array<Matrix4x4> trainTransforms = new Array<>();
    public Matrix4x4 transform_target = new Matrix4x4();
    public float t = 0;

    public Model model_tracks;
    public Matrix4x4 transform_tracks = new Matrix4x4();

    public Path path = new Path();

    public SceneRendering3D_Trains_4() {
        //path.setToQuadraticBezier(new Vector3(0,0, 0), new Vector3(15,40,0), new Vector3(30,0,0));
        float step = 0.01f;
        Vector3 p0 = new Vector3(0,0,0);
        Vector3 p1 = new Vector3(15,20,0);
        Vector3 p2 = new Vector3(30,0,0);
        path.setToQuadraticBezier(p0, p1, p2, 0.03f);

        System.out.println(path.approximateBezierLength(p0, p1, p2));

        trainTransforms.add(new Matrix4x4());
        trainTransforms.add(new Matrix4x4());
    }

    @Override
    public void setup() {

        Assets.loadModel("assets/app-models/train-car_1.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/train-rails-block.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        model_train = Assets.get("assets/app-models/train-car_1.fbx");
        model_tracks = Assets.get("assets/app-models/train-rails-block.fbx");
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, 0, 50);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        float scroll = Input.mouse.getVerticalScroll();
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.INSERT)) {
            if (camera.mode == Camera.Mode.ORTHOGRAPHIC) camera.mode = Camera.Mode.PERSPECTIVE;
            else camera.mode = Camera.Mode.ORTHOGRAPHIC;
        }
        if (Input.mouse.getVerticalScroll() != 0) {
            if (camera.mode == Camera.Mode.PERSPECTIVE) camera.translateForward(scroll * 10);
            else camera.zoom += 0.04f * scroll;
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta();
            float panVertical = Input.mouse.getYDelta();
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panVertical = Input.mouse.getYDelta();
            camera.translateForward(-panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 2,0,0,1);
            camera.rotateAroundRight(panVertical * 2);
        }
        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,1,1,1);

        Renderer3D.begin(camera);


        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            Matrix4x4 src = transform_train;
            Matrix4x4 dst = new Matrix4x4();
            Vector3 p0 = new Vector3(0,0,0);
            Vector3 p1 = new Vector3(15,20,0);
            Vector3 p2 = new Vector3(30,0,0);
            path.calculateTransform(p0, p1, p2, t, dst);
            Matrix4x4.interpolate(src, dst, t, transform_train);
            t += Graphics.getDeltaTime();
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            Vector3 p0 = new Vector3(0,0,0);
            Vector3 p1 = new Vector3(15,20,0);
            Vector3 p2 = new Vector3(30,0,0);

            Matrix4x4 src1 = trainTransforms.get(0);
            Matrix4x4 dst1 = new Matrix4x4();
            path.calculateTransform(p0, p1, p2, t, dst1);
            Matrix4x4.interpolate(src1, dst1, t, src1);

            Matrix4x4 src2 = trainTransforms.get(1);
            Matrix4x4 dst2 = new Matrix4x4();
            path.calculateTransform(p0, p1, p2, t - 0.1f, dst2);
            Matrix4x4.interpolate(src2, dst2, t - 0.1f, src2);

            t += Graphics.getDeltaTime();
        }



        for (Matrix4x4 transform : trainTransforms) {
            for (int i = 0; i < model_train.meshes.length; i++) {
                Renderer3D.drawModel_tmp_5(model_train.meshes[i], model_train.materials[i], transform);
            }
        }

//        for (int i = 0; i < model_train.meshes.length; i++) {
//            Renderer3D.drawModel_tmp_5(model_train.meshes[i], model_train.materials[i], transform_train);
//        }

        for (int i = 0; i < path.positions.size; i++) {
            Matrix4x4 transform = new Matrix4x4();
            Vector3 position = path.positions.get(i);
            Vector3 direction = path.directions.get(i);
            Vector3 up = Vector3.Z_UNIT;
            Vector3 b1 = new Vector3(direction).crs(up);
            transform.setFromBasis(b1, direction, up, position);
            for (int j = 0; j < model_tracks.meshes.length; j++) {
                Renderer3D.drawModel_tmp_5(model_tracks.meshes[j], model_tracks.materials[j], transform);
            }
        }


        Renderer3D.end();
    }

    class Path {

        public Array<Vector3> positions = new Array<>();
        public Array<Vector3> directions = new Array<>();

        public void calculateTransform(Vector3 p0, Vector3 p1, Vector3 p2, float t, Matrix4x4 out) {
            Vector3 position = new Vector3();
            position.x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
            position.y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;

            Vector3 direction = new Vector3();
            direction.x = 2 * (1 - t) * (p1.x - p0.x) + 2 * t * (p2.x - p1.x);
            direction.y = 2 * (1 - t) * (p1.y - p0.y) + 2 * t * (p2.y - p1.y);
            direction.nor();

            out.idt();
            Vector3 up = Vector3.Z_UNIT;
            Vector3 b1 = new Vector3(direction).crs(up);
            out.setFromBasis(b1, direction, up, position);
        }

        // p1 = control point
        public void setToQuadraticBezier(Vector3 p0, Vector3 p1, Vector3 p2, float step) {
            positions.clear();
            directions.clear();
            final float TRACK_LENGTH = 3.50517f;
            float t = 0f;
            do {
                Vector3 p = new Vector3();
                p.x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
                p.y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;
                t += step;
                positions.add(p);

                Vector3 d = new Vector3();
                d.x = 2 * (1 - t) * (p1.x - p0.x) + 2 * t * (p2.x - p1.x);
                d.y = 2 * (1 - t) * (p1.y - p0.y) + 2 * t * (p2.y - p1.y);
                d.nor();
                directions.add(d);
            } while (t <= 1.0f);
        }

        public void setToLine(Vector3 start, Vector3 end) {
            positions.clear();

        }

        public void setToCircle(Vector3 center, Vector3 up, float r, float angle) {
            positions.clear();

        }

        public float approximateBezierLength(Vector3 p0, Vector3 p1, Vector3 p2) {

            Vector3 v = new Vector3();
            Vector3 w = new Vector3();

            v.x = 2*(p1.x - p0.x);
            v.y = 2*(p1.y - p0.y);
            v.z = 2*(p1.z - p0.z);
            w.x = p2.x - 2*p1.x + p0.x;
            w.y = p2.y - 2*p1.y + p0.y;
            w.z = p2.z - 2*p1.z + p0.z;

            float uu = 4*(w.x*w.x + w.y*w.y + w.z*w.z);

            if(uu < 0.00001) return (float) Math.sqrt((p2.x - p0.x)*(p2.x - p0.x) + (p2.y - p0.y)*(p2.y - p0.y) + (p2.z - p0.z)*(p2.z - p0.z));


            float vv = 4*(v.x*w.x + v.y*w.y + v.z*w.z);
            float ww = v.x*v.x + v.y*v.y + v.z*v.z;

            float t1 = (float) (2*Math.sqrt(uu*(uu + vv + ww)));
            float t2 = 2*uu+vv;
            float t3 = vv*vv - 4*uu*ww;
            float t4 = (float) (2*Math.sqrt(uu*ww));

            return (float) ((t1*t2 - t3*Math.log(t2+t1) -(vv*t4 - t3*Math.log(vv+t4))) / (8*Math.pow(uu, 1.5)));
        }

    }


}
