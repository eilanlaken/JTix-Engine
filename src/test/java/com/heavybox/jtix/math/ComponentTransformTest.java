package com.heavybox.jtix.math;

import com.heavybox.jtix.ecs.ComponentTransform_2;
import org.junit.jupiter.api.Test;

// TODO: implement
class ComponentTransformTest {

    @Test
    void test_local() {
        ComponentTransform_2 t = new ComponentTransform_2(0,0,0,0,0,0,1,1,1);

        t.translate(1,1,1,true);

    }

    @Test
    void origin() {
    }

    @Test
    void update() {
    }

    @Test
    void reset() {
    }
}