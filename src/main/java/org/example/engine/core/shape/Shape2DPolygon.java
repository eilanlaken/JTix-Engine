package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsTuple2;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Shape2DPolygon extends Shape2D {

    public final int vertexCount;
    public final float[] vertices;
    public final int[] indices;
    public final boolean isConvex;
    @Deprecated private float[] worldVertices_flat;
    private final CollectionsArray<MathVector2> worldVertices;

    public final int[] holes;
    public final boolean hasHoles;
    private final CollectionsArray<CollectionsTuple2<Integer, Integer>> loops;

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    protected Shape2DPolygon(int[] indices, float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;

        // todo: see if you can rid of flat.
        this.worldVertices_flat = new float[vertices.length];
        this.worldVertices = new CollectionsArray<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new MathVector2());
        }

        this.indices = indices;
        this.isConvex = ShapeUtils.isPolygonConvex(vertices);
        // TODO: fix the area calculations.
        this.unscaledArea = Math.abs(ShapeUtils.incorrect_calculatePolygonSignedArea(this.vertices));
        this.unscaledBoundingRadius = ShapeUtils.calculatePolygonBoundingRadius(this.vertices);
        this.holes = null;
        this.loops = ShapeUtils.getLoops(null, vertexCount);
        this.hasHoles = false;
    }

    public Shape2DPolygon(float[] vertices) {
        this(vertices, null);
    }

    /**
     * @param vertices is a flat array of vertex coordinates like [x0,y0, x1,y1, x2,y2, ...].
     * @param holes is an array of hole indices if any (e.g. [5, 8] for a 12-vertex input would mean one hole with vertices 5-7 and another with 8-11).
     */
    public Shape2DPolygon(float[] vertices, int[] holes) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        if (holes != null && !CollectionsUtils.isSorted(holes, true)) Arrays.sort(holes);
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;

        // todo: see if you can rid of flat.
        this.worldVertices_flat = new float[vertices.length];
        this.worldVertices = new CollectionsArray<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new MathVector2());
        }

        this.indices = ShapeUtils.triangulatePolygon(this.vertices, holes, 2);
        this.holes = holes;
        this.loops = ShapeUtils.getLoops(holes, vertexCount);
        this.hasHoles = holes != null && holes.length != 0;
        this.isConvex = !hasHoles && ShapeUtils.isPolygonConvex(vertices);
        // TODO: fix this one and write unit tests.
        this.unscaledArea = Math.abs(ShapeUtils.incorrect_calculatePolygonSignedArea(this.vertices));
        this.unscaledBoundingRadius = ShapeUtils.calculatePolygonBoundingRadius(this.vertices);
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected void updateWorldCoordinates() {
        if (MathUtils.isZero(angle)) {
            for (int i = 0; i < vertexCount; i++) {
                worldVertices.get(i).set(vertices[i * 2] * scaleX + x, vertices[i * 2 + 1] * scaleY + y);
            }
        } else {
            MathVector2 vertex = new MathVector2();
            for (int i = 0; i < vertexCount; i++) {
                vertex.set(vertices[i * 2] * scaleX, vertices[i * 2 + 1] * scaleY).rotateDeg(angle).add(x, y);
                worldVertices.get(i).set(vertex);
            }
        }


        // TODO: this part is outdated. remove later.
        if (MathUtils.isZero(angle)) {
            for (int i = 0; i < vertices.length - 1; i += 2) {
                worldVertices_flat[i] = vertices[i] * scaleX + x;
                worldVertices_flat[i + 1] = vertices[i + 1] * scaleY + y;
            }
        } else {
            MathVector2 vertex = new MathVector2();
            for (int i = 0; i < vertices.length - 1; i += 2) {
                vertex.set(vertices[i] * scaleX, vertices[i + 1] * scaleY).rotateDeg(angle).add(x, y);
                worldVertices_flat[i] = vertex.x;
                worldVertices_flat[i + 1] = vertex.y;
            }
        }
    }

    @Deprecated public float[] getWorldVertices_flat() {
        if (!updated) update();
        return worldVertices_flat;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        return worldVertices;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        boolean inside = false;
        for (int i = 0, j = worldVertices_flat.length - 2; i < worldVertices_flat.length; i += 2) {
            float x1 = worldVertices_flat[i];
            float y1 = worldVertices_flat[i+1];
            float x2 = worldVertices_flat[j];
            float y2 = worldVertices_flat[j+1];
            if ( ((y1 > y) != (y2 > y)) )
                if (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1) inside = !inside;
            j = i;
        }
        return inside;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    public void getWorldEdge(int index, @NotNull MathVector2 tail, @NotNull MathVector2 head) {
        if (!hasHoles) {
            getWorldVertex(index, tail);
            getWorldVertex(index + 1, head);
            return;
        }

        getWorldVertex(index, tail);
        int next = index + 1;
        for (CollectionsTuple2<Integer, Integer> loop : loops) {
            if (index == loop.t2) next = loop.t1;
        }
        getWorldVertex(next, head);
    }

    public MathVector2 getWorldVertex(int index, MathVector2 out) {
        if (!updated) update();
        if (out == null) out = new MathVector2();
        int n2 = worldVertices_flat.length / 2;
        if (index >= n2) return out.set(worldVertices_flat[(index % n2) * 2], worldVertices_flat[(index % n2) * 2 + 1]);
        else if (index < 0) return out.set(worldVertices_flat[(index % n2 + n2) * 2], worldVertices_flat[(index % n2 + n2) * 2 + 1]);
        else return out.set(worldVertices_flat[index * 2], worldVertices_flat[index * 2 + 1]);
    }

    public float getWorldVertexX(int index) {
        if (!updated) update();
        int n2 = worldVertices_flat.length / 2;
        if (index >= n2) return worldVertices_flat[(index % n2) * 2];
        else if (index < 0) return worldVertices_flat[(index % n2 + n2) * 2];
        else return worldVertices_flat[index * 2];
    }

    public float getWorldVertexY(int index) {
        if (!updated) update();
        int n2 = worldVertices_flat.length / 2;
        if (index >= n2) return worldVertices_flat[(index % n2) * 2 + 1];
        else if (index < 0) return worldVertices_flat[(index % n2 + n2) * 2 + 1];
        else return worldVertices_flat[index * 2 + 1];
    }

    public static float getVertexX(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2];
        else if (index < 0) return vertices[(index % n2 + n2) * 2];
        return vertices[index * 2];
    }

    public static float getVertexY(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2 + 1];
        else if (index < 0) return vertices[(index % n2 + n2) * 2 + 1];
        return vertices[index * 2 + 1];
    }

}