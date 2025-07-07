package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.math.Matrix4x4;

public class GameObject {

    public Matrix4x4 transform;
    public Model model;
    public Array<Logic> logics = new Array<>();

    public GameObject(Matrix4x4 transform, Model model) {
        this.transform = transform;
        this.model = model;
    }

    public static abstract class Logic {

        public abstract void start();
        public abstract void update();

    }

}
