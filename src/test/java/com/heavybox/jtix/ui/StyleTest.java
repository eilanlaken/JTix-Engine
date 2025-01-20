package com.heavybox.jtix.ui;

import com.heavybox.jtix.widgets.Style;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class StyleTest {

    @Test
    void testClone() {
        Style s1 = new Style();
        s1.boxMarginBottom = -90;
        s1.transform = null;
        s1.boxPaddingRight = 3;
        Style s2 = s1.clone();
        Assertions.assertEquals(-90, s2.boxMarginBottom);
        Assertions.assertNull(s2.transform);
        int oldPaddingRight = s1.boxPaddingRight;
        Assertions.assertEquals(oldPaddingRight, s2.boxPaddingRight);
        s1.boxPaddingRight = 40;
        Assertions.assertEquals(oldPaddingRight, s2.boxPaddingRight);
    }

}