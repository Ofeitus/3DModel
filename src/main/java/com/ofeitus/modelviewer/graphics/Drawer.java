package com.ofeitus.modelviewer.graphics;

import java.awt.*;

public class Drawer {
    private static int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }

    public static void drawLine(int xStart, int yStart, int xEnd, int yEnd, Graphics g) {
        int x;
        int y;
        int dx;
        int dy;
        int incx;
        int incy;
        int pdx;
        int pdy;
        int es;
        int el;
        int err;

        dx = xEnd - xStart;
        dy = yEnd - yStart;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) {
            dx = -dx;
        }

        if (dy < 0) {
            dy = -dy;
        }

        if (dx > dy) {
            pdx = incx;
            pdy = 0;
            es = dy;
            el = dx;
        } else {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xStart;
        y = yStart;
        err = el/2;
        g.drawLine(x, y, x, y);

        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;
                y += incy;
            } else {
                x += pdx;
                y += pdy;
            }
            g.drawLine (x, y, x, y);
        }
    }

    public static void drawPolygon(int x1, int y1, int x2, int y2, int x3, int y3, Graphics g)
    {
        if (y2 < y1) {
            y1 = y1 ^ y2 ^ (y2 = y1);
            x1 = x1 ^ x2 ^ (x2 = x1);
        }
        if (y3 < y1) {
            y1 = y1 ^ y3 ^ (y3 = y1);
            x1 = x1 ^ x3 ^ (x3 = x1);
        }
        if (y2 > y3) {
            y2 = y2 ^ y3 ^ (y3 = y2);
            x2 = x2 ^ x3 ^ (x3 = x2);
        }

        double dx13;
        double dx12;
        double dx23;
        if (y3 != y1) {
            dx13 = (double) x3 - x1;
            dx13 /= y3 - y1;
        }
        else
        {
            dx13 = 0;
        }

        if (y2 != y1) {
            dx12 = (double) x2 - x1;
            dx12 /= (y2 - y1);
        }
        else
        {
            dx12 = 0;
        }

        if (y3 != y2) {
            dx23 = x3 - x2;
            dx23 /= (y3 - y2);
        }
        else
        {
            dx23 = 0;
        }

        double wx1 = x1;
        double wx2 = wx1;
        double _dx13 = dx13;


        if (dx13 > dx12)
        {
            double tmp;
            tmp = dx12;
            dx12 = dx13;
            dx13 = tmp;
        }
        for (int i = y1; i < y2; i++){
            for (int j = (int) wx1; j <= wx2; j++){
                drawLine(j, i, j, i, g);
            }
            wx1 += dx13;
            wx2 += dx12;
        }
        if (y1 == y2){
            wx1 = x1;
            wx2 = x2;
        }
        if (_dx13 < dx23)
        {
            double tmp;
            tmp = _dx13;
            _dx13 = dx23;
            dx23 = tmp;
        }
        for (int i = y2; i <= y3; i++){
            for (int j = (int) wx1; j <= wx2; j++){
                drawLine(j, i, j, i, g);
            }
            wx1 += _dx13;
            wx2 += dx23;
        }
    }
}
