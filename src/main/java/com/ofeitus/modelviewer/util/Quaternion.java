package com.ofeitus.modelviewer.util;

public class Quaternion {
    public double x, y, z, w;

    public static double length(Quaternion q) {
        return Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w);
    }

    public static Quaternion normalize(Quaternion q) {
        double l = length(q);
        q.x /= l;
        q.y /= l;
        q.z /= l;
        q.w /= l;
        return q;
    }

    public static Quaternion conjugate(Quaternion q) {
        q.x = -q.x;
        q.y = -q.y;
        q.z = -q.z;
        return q;
    }

    public static Quaternion multiply(Quaternion a, Quaternion b) {
        Quaternion c = new Quaternion();
        c.x = a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y;
        c.y = a.w*b.y - a.x*b.z + a.y*b.w + a.z*b.x;
        c.z = a.w*b.z + a.x*b.y - a.y*b.x + a.z*b.w;
        c.w = a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z;
        return c;
    }
}
