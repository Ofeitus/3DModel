package com.ofeitus.modelviewer.model;

import java.util.Objects;

public class Line2D {
    double x1;
    double y1;
    double x2;
    double y2;

    public Line2D(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line2D line2D = (Line2D) o;
        return (Double.compare(line2D.x1, x1) == 0 &&
                Double.compare(line2D.y1, y1) == 0 &&
                Double.compare(line2D.x2, x2) == 0 &&
                Double.compare(line2D.y2, y2) == 0) ||
                (Double.compare(line2D.x1, x2) == 0 &&
                Double.compare(line2D.y1, y2) == 0 &&
                Double.compare(line2D.x2, x1) == 0 &&
                Double.compare(line2D.y2, y1) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1) + Objects.hash(x2, y2);
    }
}
