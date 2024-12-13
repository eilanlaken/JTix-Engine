package com.heavybox.jtix.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Tuple4Test {

    @Test
    public void testMap() {
        Map<Tuple4<Integer, Integer, Boolean, Boolean>, String> map = new HashMap<>();

        Tuple4<Integer, Integer, Boolean, Boolean> tup1 = new Tuple4<>(4,4,false,false);
        Tuple4<Integer, Integer, Boolean, Boolean> tup2 = new Tuple4<>(4,4,false,false);
        Tuple4<Integer, Integer, Boolean, Boolean> tup3 = new Tuple4<>(1,4,true,false);
        map.put(tup1, "hi");
        map.put(tup2, "bye");
        map.put(tup3, "ok");
        Assertions.assertEquals("bye", map.get(tup1));
        Assertions.assertEquals("bye", map.get(tup2));
        Assertions.assertEquals("ok", map.get(tup3));
    }

}
