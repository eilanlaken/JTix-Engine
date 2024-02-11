package org.example.engine.core.math;

public class Shape3DPlane implements Shape3D {

    public Vector3 normal;
    public float d;

    // a plane with normal = normal and distance from the origin = d
    public Shape3DPlane(Vector3 normal, float d) {
        this.normal = new Vector3(normal);
        this.normal.normalize();
        this.d = d;
    }

    // a plane with a normal = normal and a point on the plane pointOnPlane
    public Shape3DPlane(Vector3 normal, Vector3 pointOnPlane) {
        this.normal = new Vector3(normal);
        this.normal.normalize();
        this.d = -1 * Vector3.dot(normal, pointOnPlane);
    }

    // a plane with a,b and c points on the plane.
    public Shape3DPlane(Vector3 a, Vector3 b, Vector3 c) {
        this.normal = new Vector3();
        normal.set(a).sub(b).cross(b.x - c.x, b.y - c.y, b.z - c.z).normalize();
        d = -1 * Vector3.dot(a, normal);
    }

    public void set(float nx, float ny, float nz, float d) {
        normal.set(nx, ny, nz);
        this.d = d;
    }

    public float distance(final Vector3 point) {
        return Vector3.dot(normal, point) + d;
    }


    public float distance(float x, float y, float z) {
        return normal.x * x + normal.y * y + normal.z * z + d;
    }

    public short getSide(final Vector3 point) {
        float distance = Vector3.dot(normal, point) + d;
        if (distance < 0) return -1;
        if (distance == 0) return 0;
        else return 1;
    }

    public short getSide(float x, float y, float z) {
        float distance = normal.x * x + normal.y * y + normal.z * z + d;
        if (distance < 0) return -1;
        if (distance == 0) return 0;
        else return 1;
    }

    public void set(Vector3 point, Vector3 normal) {
        this.normal.set(normal);
        d =  -1 * Vector3.dot(normal, point);
    }

    public void set(float pointX, float pointY, float pointZ, float norX, float norY, float norZ) {
        this.normal.set(norX, norY, norZ);
        d = -(pointX * norX + pointY * norY + pointZ * norZ);
    }

    public String toString () {
        return "<Plane: " + normal.toString() + " | " + d + ">";
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return distance(x,y,z) == 0;
    }
}