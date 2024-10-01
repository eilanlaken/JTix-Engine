package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class ComponentLogicsScripts implements ComponentLogics {

    public Array<ComponentLogicsScript> scripts;

    public ComponentLogicsScripts(ComponentLogicsScript... scripts) {
        this.scripts = new Array<>(scripts);
    }

}
