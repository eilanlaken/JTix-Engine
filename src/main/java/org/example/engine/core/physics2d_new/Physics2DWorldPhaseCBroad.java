package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsArrayConcurrent;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

import java.util.HashSet;
import java.util.Set;

// this should be multithreaded
public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    protected final MemoryPool<Cell>       cellMemoryPool = new MemoryPool<>(Cell.class, 1024);
    protected final int                    processors     = AsyncUtils.getAvailableProcessorsNum();

    Physics2DWorldPhaseCBroad() {

    }

    @Override
    public void update(Physics2DWorld world, float delta) {
        cellMemoryPool.freeAll(world.spacePartition);
        world.spacePartition.clear();
        if (world.allBodies.isEmpty()) return;
        //if (world.allBodies.size == 1) return; // TODO: put back.

        // data from previous phase
        final float minX = world.worldMinX;
        final float maxX = world.worldMaxX;
        final float minY = world.worldMinY;
        final float maxY = world.worldMaxY;

        final float cellWidth  = world.cellWidth;
        final float cellHeight = world.cellHeight;

        final int rows = world.rows;
        final int cols = world.cols;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cellMemoryPool.allocate();
                cell.x = minX + (j + 0.5f) * cellWidth;
                cell.y = minY + (i + 0.5f) * cellHeight;
                world.spacePartition.add(cell);
            }
        }

        // FIXME: populate cells
        for (Physics2DBody body : world.allBodies) {

            int startCol = (int) ((body.shape.getMinExtentX() - minX) / cellWidth);
            int endCol = (int) ((body.shape.getMaxExtentX() - minX) / cellWidth);
            int startRow = (int) ((body.shape.getMinExtentY() - minY) / cellHeight);
            int endRow = (int) ((body.shape.getMaxExtentY() - minY) / cellHeight);

            startCol = Math.max(startCol, 0);
            endCol = Math.min(endCol, cols - 1);
            startRow = Math.max(startRow, 0);
            endRow = Math.min(endRow, rows - 1);

            if (true) return;
            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    int cellIndex = row * cols + col; // Convert 2D cell coordinates to 1D index
                    Cell cell = world.spacePartition.get(cellIndex);
                    cell.x = minX + (col + 0.5f) * cellWidth;
                    cell.y = minY + (row + 0.5f) * cellHeight;
                    cell.bodies.add(body);
                    world.activeCells.add(cell);
                }
            }

        }


        // run broad phase. Use async tasks.

        // merge all collision candidates.
        CollectionsArray<Physics2DBody> collisionCandidates = world.collisionCandidates;

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArrayConcurrent<Physics2DBody> bodies     = new CollectionsArrayConcurrent<>(false, 2);
        private final CollectionsArrayConcurrent<Physics2DBody> candidates = new CollectionsArrayConcurrent<>(false, 2);

        protected float x;
        protected float y;

        public Cell() {}

        private boolean areBoundingCirclesCollide(final Shape2D a, final Shape2D b) {
            final float dx  = b.x() - a.x();
            final float dy  = b.y() - a.y();
            final float sum = a.getBoundingRadius() + b.getBoundingRadius();
            return dx * dx + dy * dy < sum * sum;
        }

        @Override
        public void reset() {
            bodies.clear();
            candidates.clear();
        }

    }

}


//TODO: remove
/*
int min_i_index  = (int) Math.floor((max_body_y - maxY) / cellHeight);
            int max_i_index  = (int) Math.ceil((min_body_y - maxY) / cellHeight);
            int min_j_index  = (int) Math.floor((min_body_x - minX) / cellWidth);
            int max_j_index  = (int) Math.ceil((max_body_x - minX) / cellWidth);
            int i = min_i_index;
            while (i < max_i_index) {
                int j = min_j_index;
                while (j < max_j_index) {
                    Cell cell = spacePartition.get(i * cols + j);
                    cell.bodies.add(body);
                    cell.x = minX + j * cellWidth;
                    cell.y = maxY - i * cellHeight;
                    world.activeCells.add(cell);
                    j += cellWidth;
                }
                i += cellHeight;
            }


 */

// TODO: remove
/**
 *
 // build partition
 for (Physics2DBody body : world.allBodies) {
 float min_body_x = body.shape.getMinExtentX();
 float max_body_x = body.shape.getMaxExtentX();
 float min_body_y = body.shape.getMinExtentY();
 float max_body_y = body.shape.getMaxExtentY();
 int min_i_index  = (int) Math.floor((min_body_y - minY) / cellHeight);
 int max_i_index  = (int) Math.floor((max_body_y - minY) / cellHeight);
 int min_j_index  = (int) Math.floor((min_body_x - minX) / cellWidth);
 int max_j_index  = (int) Math.floor((max_body_x - minX) / cellWidth);
 int i = min_i_index;
 while (i < max_i_index) {
 int j = min_j_index;
 while (j < max_j_index) {
 //Cell cell = partition[i * cellCount + j];
 //cell.bodies.add(body);
 //activeCells.add(cell);
 j++;
 }
 i++;
 }
 }


 */