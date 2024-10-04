package com.heavybox.jtix.ecs_2;

public final class ECSUtils {

    private ECSUtils() {}

    public static int getComponentsBitmask(final Entity entity) {
        int mask = 0;

        Component component;
        for (Component.Type type : Component.Type.values()) {
            component = entity.getComponent(type);
            if (component != null) mask |= component.getBitmask();
        }

        return mask;
    }

    public static int getCategoryBitmask(final Enum category) {
        return 0b000001 << category.ordinal();
    }

}

/*
public static int getComponentsBitmask(final Entity entity) {
    Component audio = entity.getComponentAudio();
        int mask = 0;

        Component audio = entity.getComponentRender();
        if (audio != null) mask |= audio.getBitmask();

        Component render = entity.getComponentRender();
        if (render != null) mask |= render.getBitmask();

        Component camera = entity.getComponentCamera();
        if (camera != null) mask |= camera.getBitmask();

        Component logics = entity.getComponentLogics();
        if (logics != null) mask |= logics.getBitmask();

        Component physics = entity.getComponentPhysics();
        if (physics != null) mask |= physics.getBitmask();

        Component region = entity.getComponentRegion();
        if (region != null) mask |= region.getBitmask();

        Component signals = entity.getComponentSignals();
        if (signals != null) mask |= signals.getBitmask();

        Component transform = entity.getComponentTransform();
        if (transform != null) mask |= transform.getBitmask();

        return mask;
}
 */