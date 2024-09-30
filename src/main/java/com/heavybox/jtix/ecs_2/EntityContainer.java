package com.heavybox.jtix.ecs_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.GraphicsUtils;

public class EntityContainer {

    /* State */
    protected Array<Entity> entities = new Array<>(false, 200);
    protected Array<Entity> toAdd    = new Array<>(false, 50);
    protected Array<Entity> toRemove = new Array<>(false, 50);

    private float secondsPerUpdate = 1 / 60.0f;
    private float lag              = 0;

    /* Systems */
    protected final Array<System>   systems   = new Array<>();
    protected final SystemDynamics  dynamics  = new SystemDynamics(this);
    protected final SystemGUI       gui       = new SystemGUI(this);
    protected final SystemAudio     audio     = new SystemAudio(this);
    protected final SystemRendering rendering = new SystemRendering(this);
    protected final SystemScripting scripting = new SystemScripting(this);
    protected final SystemSignaling signaling = new SystemSignaling(this);

    /* Component Store */
    protected Array<ComponentTransform> transforms = new Array<>(false, 200);
    protected Array<ComponentGraphics>  graphics   = new Array<>(false, 200);
    protected Array<ComponentAudio>     audios     = new Array<>(false, 200);
    protected Array<ComponentPhysics>   physics    = new Array<>(false, 200);
    protected Array<ComponentScripts>   scripts    = new Array<>(false, 200);
    protected Array<ComponentSignals>   signals    = new Array<>(false, 200);

    public EntityContainer() {
        systems.add(dynamics);
        systems.add(audio);
        systems.add(gui);
        systems.add(rendering);
        systems.add(scripting);
        systems.add(signaling);
    }

    public void update() {
        float elapsedTime = GraphicsUtils.getDeltaTime();
        this.lag += elapsedTime;

        // call the fixedUpdate() of every system
        while (this.lag >= this.secondsPerUpdate) {
            removeEntities();
            addEntities();
            for (System system : systems) {
                system.fixedUpdate(this.secondsPerUpdate);
            }
            this.lag -= this.secondsPerUpdate;
        }

        // call the frameUpdate() of every system
        for (System system : systems) {
            system.frameUpdate(elapsedTime);
        }
    }

    // TODO: implement
    private void removeEntities() {

    }

    // TODO: implement
    private void addEntities() {

    }

}
