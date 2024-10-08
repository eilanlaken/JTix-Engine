package com.heavybox.jtix.ecs;

public final class ECSUtils {

    private ECSUtils() {}

    static int getComponentsBitmask(final Entity entity) {
        int mask = 0;
        Component component;
        for (Component.Type type : Component.Type.values()) {
            component = entity.getComponent(type);
            if (component != null) mask |= component.getBitmask();
        }
        return mask;
    }

}
