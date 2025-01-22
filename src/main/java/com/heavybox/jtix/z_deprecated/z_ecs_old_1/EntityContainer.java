package com.heavybox.jtix.z_deprecated.z_ecs_old_1;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;

public class EntityContainer {

    /* Entities */
    protected Array<Entity> entities = new Array<>(false, 200);
    protected Array<Entity> toAdd    = new Array<>(false, 50);
    protected Array<Entity> toRemove = new Array<>(false, 50);

    /* State */
    private float secondsPerUpdate = 1 / 60.0f;
    private float lag              = 0;

    /* Systems */
    protected final Array<System>   systems   = new Array<>(true, 6);
    protected final SystemDynamics  dynamics  = new SystemDynamics(this);
    protected final SystemGUI       gui       = new SystemGUI(this);
    protected final SystemAudio     audio     = new SystemAudio(this);
    protected final SystemRendering rendering = new SystemRendering(this);
    protected final SystemLogic     scripting = new SystemLogic(this);
    protected final SystemSignaling signaling = new SystemSignaling(this);

    /* Component Store */
    protected Array<ComponentTransform> transforms = new Array<>(false, 200);
    protected Array<ComponentGraphics>  graphics   = new Array<>(false, 200);
    protected Array<ComponentAudio>     audios     = new Array<>(false, 200);
    protected Array<ComponentPhysics>   physics    = new Array<>(false, 200);
    protected Array<ComponentLogics>    logics     = new Array<>(false, 200);
    protected Array<ComponentSignals>   signals    = new Array<>(false, 200);
    protected Array<ComponentRegion>    regions    = new Array<>(false, 200);

    public EntityContainer() {
        systems.add(dynamics);
        systems.add(audio);
        systems.add(gui);
        systems.add(rendering);
        systems.add(scripting);
        systems.add(signaling);
    }

    public void update() {
        float elapsedTime = Graphics.getDeltaTime();
        this.lag += elapsedTime;

        /* call the fixedUpdate() of every system */
        while (this.lag >= this.secondsPerUpdate) {
            removeEntities();
            addEntities();
            for (System system : systems) {
                if (!system.active) continue;
                system.fixedUpdate(this.secondsPerUpdate);
            }
            this.lag -= this.secondsPerUpdate;
        }

        /* call the frameUpdate() of every system */
        for (System system : systems) {
            if (!system.active) continue;
            system.frameUpdate(elapsedTime);
        }
    }

    private void removeEntities() {
        for (Entity entity : toRemove) {
            for (System system : systems) {
                if (system.shouldProcess(entity) && entity.handle != -1) system.remove(entity);
            }
        }
        for (Entity entity : toRemove) {
            if (entity.handle == -1) throw new ECSException("Trying to remove an " + Entity.class.getSimpleName() + " that was not inserted");

            int handle = entity.handle;
            /* remove from components store */
            transforms.removeIndex(handle);
            graphics.removeIndex(handle);
            audios.removeIndex(handle);
            physics.removeIndex(handle);
            logics.removeIndex(handle);
            signals.removeIndex(handle);
            regions.removeIndex(handle);
            /* remove from entities array and update the handles of the affected Entities */
            entities.removeIndex(handle);
            entity.handle = -1;
            entity.container = null;
            if (entities.isEmpty()) continue;
            entities.get(handle).handle = handle;
        }
        toRemove.clear();
    }

    private void addEntities() {
        for (Entity entity : toAdd) {
            if (entity.handle != -1) throw new ECSException("Trying to add an already present " + Entity.class.getSimpleName());

            entity.handle = entities.size;
            entity.container = this;
            entities.add(entity);
            ComponentTransform cTransform = entity.createComponentTransform();
            ComponentAudio cAudio = entity.createComponentAudio();
            ComponentGraphics cGraphics = entity.createComponentGraphics();
            ComponentPhysics cPhysics = entity.createComponentPhysics();
            ComponentLogics cLogics = entity.createComponentLogics();
            ComponentSignals cSignals = entity.createComponentSignals();
            ComponentRegion cRegion = entity.createComponentRegion();
            transforms.add(cTransform);
            audios.add(cAudio);
            graphics.add(cGraphics);
            physics.add(cPhysics);
            logics.add(cLogics);
            signals.add(cSignals);
            regions.add(cRegion);

            // TODO: compute the entity's bitmask
        }
        for (Entity entity : toAdd) {
            for (System system : systems) {
                if (system.shouldProcess(entity)) system.add(entity);
            }
        }
        toAdd.clear();
    }

    public void setSecondsPerUpdate(float secondsPerUpdate) {
        if (secondsPerUpdate <= 0) return;
        this.secondsPerUpdate = secondsPerUpdate;
    }

    public float getSecondsPerUpdate() {
        return secondsPerUpdate;
    }

    public void registerSystem(System system) {
        if (systems.contains(system, true)) return;
        systems.add(system);
    }

}
