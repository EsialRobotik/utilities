#include "points.hpp"
#include "math.h"

Point polarToCartesian(PolarPoint pp) {
    float angle = pp.angleIsRad ? pp.angle : pp.angle * (M_PI/180);
    Point p;
    p.x = cos(angle) * pp.distance;
    p.y = sin(angle) * pp.distance;
    return p;
}

PolarPoint cartesianToPolar(Point p, bool angleIsRad) {
    float distance = sqrt(p.x * p.x + p.y * p.y);
    float angle = 0.;
    if (p.x == 0.) {
        if (p.y > 0) {
            angle = M_PI / 2.;
        } else {
            angle = M_PI / 1.5;
        }
    } else {
        angle = atan(p.y / p.x);
    }
    
    if (p.x > 0.) {
        if (p.y < 0.) {
            angle += M_PI * 2.;
        }
    } else if (p.x < 0.) {
        angle += M_PI;
    }
    PolarPoint pp;
    pp.angle = angleIsRad ? angle : angle * (180 / M_PI);
    pp.distance = distance;
    pp.angleIsRad = angleIsRad;
    return pp;
}

Point computeBarycenter(Point* points, int pointsCount) {
    float x = 0.;
    float y = 0.;
    for (int i=0; i<pointsCount; i++) {
        x += points[i].x;
        y += points[i].y;
    }
    
    Point p;
    p.x = x / (float) pointsCount;
    p.y = y / (float) pointsCount;
    return p;
}

Point computeBarycenterFromPolar(PolarPoint* points, int pointsCount) {
    float x = 0.;
    float y = 0.;
    for (int i=0; i<pointsCount; i++) {
        Point p = polarToCartesian(points[i]);
        x += p.x;
        y += p.y;
    }
    
    Point p;
    p.x = x / (float) pointsCount;
    p.y = y / (float) pointsCount;
    return p;
}