package com.heavybox.jtix.ecs;

public final class ECSUtils {

    private ECSUtils() {}

    public static int getComponentsBitmask(final Entity entity) {
        int mask = 0;

        Component audio = entity.getComponentAudio();
        if (audio != null) mask |= audio.getBitmask();

        Component graphics = entity.getComponentGraphics();
        if (graphics != null) mask |= graphics.getBitmask();

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

    public static int getCategoryBitmask(final Enum category) {
        return 0b000001 << category.ordinal();
    }

}
