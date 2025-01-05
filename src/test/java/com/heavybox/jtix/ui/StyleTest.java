package com.heavybox.jtix.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class StyleTest {

    @Test
    void testClone() {
        Style s1 = new Style();
        s1.marginBottom = -90;
        s1.position = null;
        s1.paddingRight = 3;
        Style s2 = s1.clone();
        Assertions.assertEquals(-90, s2.marginBottom);
        Assertions.assertNull(s2.position);
        int oldPaddingRight = s1.paddingRight;
        Assertions.assertEquals(oldPaddingRight, s2.paddingRight);
        s1.paddingRight = 40;
        Assertions.assertEquals(oldPaddingRight, s2.paddingRight);
    }

}