package com.fengmap.drpeng.common;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.drpeng.entity.FMMapCoordWithAngle;

import java.util.ArrayList;

/**
 * Created by yangbin on 16/7/6.
 */
public class InterplatorUtil {
    public final static  float FACTOR = 0.001f;   // 1米插5段

    public static ArrayList<FMMapCoordWithAngle> linearValues(FMMapCoordWithAngle start, FMMapCoordWithAngle end) {
        double lenX = end.getCoord().x - start.getCoord().x;
        double lenY = end.getCoord().y - start.getCoord().y;
        float cAngle = end.getAngle() - start.getAngle();

        double stepX = Math.abs(lenX * FACTOR);
        double stepY = Math.abs(lenY * FACTOR);

        int count = (int)(stepX > stepY ? stepX : stepY);

        stepX = lenX / count;
        stepY = lenY / count;
        float stepAngle = cAngle / count;

        ArrayList<FMMapCoordWithAngle> coords = new ArrayList<>(count);

        for (int i=0; i<count; i++) {
            double x = start.getCoord().x + stepX * i;
            double y = start.getCoord().y + stepY * i;
            float a = start.getAngle() + stepAngle * i;
            coords.add(new FMMapCoordWithAngle(new FMMapCoord(x, y), a));
        }

        return coords;
    }


    public static ArrayList<FMMapCoordWithAngle> getAllLinearInterplatorValues(ArrayList<FMMapCoordWithAngle> points) {
        ArrayList<FMMapCoordWithAngle> mapCoords = new ArrayList<>(50);
        int size = points.size() - 1;
        for (int i=0; i< size; i++) {
            mapCoords.addAll(linearValues(points.get(i), points.get(i+1)));
        }
        mapCoords.trimToSize();
        return mapCoords;
    }

}
