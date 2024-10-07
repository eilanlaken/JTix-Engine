package com.heavybox.jtix.ecs_3;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.GraphicsUtils;

public class EntityContainer {

    /* Entities */
    protected Array<Entity> entities = new Array<>(false, 200);
    protected Array<Entity> toAdd    = new Array<>(false, 50);
    protected Array<Entity> toRemove = new Array<>(false, 50);

    /* State */
    private float secondsPerUpdate = 1 / 60.0f;
    private float lag              = 0;

    /* Systems */
    protected final Array<System>   systems         = new Array<>(true, 6);
    protected final SystemDynamics  systemDynamics  = new SystemDynamics(this);
    protected final SystemGUI       systemGUI       = new SystemGUI(this);
    protected final SystemAudio     systemAudio     = new SystemAudio(this);
    protected final SystemRendering systemRendering = new SystemRendering(this);
    protected final SystemLogics    systemLogics    = new SystemLogics(this);

    /* Component Store */
    protected Array<ComponentTransform> componentTransforms = new Array<>(false, 200);
    protected Array<ComponentRender>    componentRenders    = new Array<>(false, 200);
    protected Array<ComponentCamera>    componentCameras    = new Array<>(false, 200);
    protected Array<ComponentAudio>     componentAudios     = new Array<>(false, 200);
    protected Array<ComponentPhysics>   componentPhysics    = new Array<>(false, 200);
    protected Array<ComponentLogics>    componentScripts    = new Array<>(false, 200);
    protected Array<ComponentRegion>    componentRegions    = new Array<>(false, 200);

    public EntityContainer() {
        systems.add(systemDynamics);
        systems.add(systemAudio);
        systems.add(systemGUI);
        systems.add(systemRendering);
        systems.add(systemLogics);
    }

    public void update() {
        float elapsedTime = GraphicsUtils.getDeltaTime();
        this.lag += elapsedTime;

        /* call the fixedUpdate() of every system */
        while (this.lag >= this.secondsPerUpdate) {
            removeEntities();
            addEntities();
            for (System system : systems) {
                system.fixedUpdate(this.secondsPerUpdate);
            }
            this.lag -= this.secondsPerUpdate;
        }

        /* call the frameUpdate() of every system */
        for (System system : systems) {
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
            componentTransforms.removeIndex(handle);
            componentRenders.removeIndex(handle);
            componentCameras.removeIndex(handle);
            componentAudios.removeIndex(handle);
            componentPhysics.removeIndex(handle);
            componentScripts.removeIndex(handle);
            componentRegions.removeIndex(handle);
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
            ComponentRender cRender = entity.createComponentRender();
            ComponentCamera cCamera = entity.createComponentCamera();
            ComponentPhysics cPhysics = entity.createComponentPhysics();
            ComponentLogics cLogics = entity.createComponentLogics();
            ComponentRegion cRegion = entity.createComponentRegion();
            componentTransforms.add(cTransform);
            componentAudios.add(cAudio);
            componentRenders.add(cRender);
            componentCameras.add(cCamera);
            componentPhysics.add(cPhysics);
            componentScripts.add(cLogics);
            componentRegions.add(cRegion);

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
