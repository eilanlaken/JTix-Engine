package com.heavybox.jtix.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AsyncUtilsTest {

    @Test
    void sync() {
    }

    @Test
    void getAvailableProcessors() {
        Assertions.assertTrue(AsyncUtils.getAvailableProcessorsNumber() > 0);
    }
}