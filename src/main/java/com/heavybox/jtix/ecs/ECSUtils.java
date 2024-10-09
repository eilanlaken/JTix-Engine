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

    public static int getLayersBitmask(EntityLayer2D... layers) {
        int layerBitmask = 0;
        for (EntityLayer2D layer : layers) {
            layerBitmask |= layer.bitmask;
        }
        return layerBitmask;
    }

    public static int getLayersBitmask(EntityLayer3D... layers) {
        int layerBitmask = 0;
        for (EntityLayer3D layer : layers) {
            layerBitmask |= layer.bitmask;
        }
        return layerBitmask;
    }

}
