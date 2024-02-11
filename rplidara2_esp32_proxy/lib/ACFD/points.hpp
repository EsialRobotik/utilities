#ifndef POINTS_H_
#define POINTS_H_

struct PolarPoint {
    bool angleIsRad;
    float angle;
    float distance;
};

struct Point {
    float y;
    float x;
};

Point polarToCartesian(PolarPoint pp);

PolarPoint cartesianToPolar(Point p, bool angleIsRad);

Point computeBarycenter(Point* points, int pointsCount);

Point computeBarycenterFromPolar(PolarPoint* points, int pointsCount);

#endif