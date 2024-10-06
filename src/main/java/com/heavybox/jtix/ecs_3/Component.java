package com.heavybox.jtix.ecs_3;

public interface Component {

    enum Type {

        AUDIO,
        RENDER,
        CAMERA,
        LOGICS,
        PHYSICS,
        REGION,
        SIGNALS,
        TRANSFORM,
        ;

        public final int bitmask;

        Type() {
            this.bitmask = 0b000001 << ordinal();
        }

    }

    int getBitmask();

}
