package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;
import org.junit.jupiter.api.Test;

class ComponentTransformTest {

    @Test
    public void testValues() {
        ComponentTransform_old_1 t1 = new ComponentTransform_old_1(10,2,-1,10,20,30,2,0.5f,0.5f);
        Matrix4x4 m1 = t1.matrix();
    }

}