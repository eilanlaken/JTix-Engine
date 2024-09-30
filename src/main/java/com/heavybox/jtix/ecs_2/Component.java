package com.heavybox.jtix.ecs_2;

public interface Component {

    enum Type {

        AUDIO,
        GRAPHICS,
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
