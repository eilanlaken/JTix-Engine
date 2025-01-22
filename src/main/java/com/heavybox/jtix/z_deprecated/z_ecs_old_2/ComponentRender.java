package com.heavybox.jtix.z_deprecated.z_ecs_old_2;

public interface ComponentRender extends Component {

    @Override
    default int getBitmask() {
        return Type.RENDER.bitmask;
    }

}
