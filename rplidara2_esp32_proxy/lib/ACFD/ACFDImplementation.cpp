#include "ACFDImplementation.hpp"
#include <math.h>
#include <Arduino.h>
ACFDImplementation::ACFDImplementation(	    
    float ignoreDistanceAbove,
    int minPointsPerCluster,
    int maxPointsPerCluster,
    int maxClusterCount,
    float maxDistanceBetween2Pointsmm,
    float maxAngleBetween2PointsDegrees,
    int maxPointsToSkip
):
    ignoreDistanceAbove(ignoreDistanceAbove),
    minPointsPerCluster(minPointsPerCluster),
    maxPointsPerCluster(maxPointsPerCluster),
    maxClusterCount(maxClusterCount),
    maxDistanceBetween2Pointsmm(maxDistanceBetween2Pointsmm),
    maxAngleBetween2PointsDegrees(maxAngleBetween2PointsDegrees),
    maxPointsToSkip(maxPointsToSkip),
    clusterCoordinatesReadCursor(-1),
    clusterCoordinatesWriteCursor(-1),
    currentClusterPointsIndex(-1)
{
    for (int i=POINTS_TOTAUX-1; i>=0; i--) {
        points[i] = 0.;
    }
    clustersCoordinates = (Point*) malloc(sizeof(Point*) * maxClusterCount);
    currentClusterPoints = (PolarPoint*) malloc(sizeof(PolarPoint*) * maxPointsPerCluster);
}

void ACFDImplementation::addPoint(float angle, float distance) {
    unsigned int index = (unsigned int) (angle * POINTS_PAR_DEGRES);
    if (index < POINTS_TOTAUX) {
        points[index] = distance;
    }
}

bool ACFDImplementation::hasNextClusterCoordinates() {
    return clusterCoordinatesReadCursor < clusterCoordinatesWriteCursor;
}

Point ACFDImplementation::nextClusterCoordinates() {
    return clustersCoordinates[++clusterCoordinatesReadCursor];
}

void ACFDImplementation::doClustering() {
    clusterCoordinatesReadCursor = -1;
    clusterCoordinatesWriteCursor = -1;
    currentClusterPointsIndex = -1;
    int emptyPoints = 0;

    // On parcourt tous les points jusqu'à arriver au bout de la liste ou avoir trouvé le nombre max de clusters
    for (int i=0; i<POINTS_TOTAUX; i++) {
        float angle = (float) i / (float) POINTS_PAR_DEGRES;
        float distance = points[i];
        points[i] = 0.;
        if (currentClusterPointsIndex == -1) {
            if (distance > 0.1) {
                PolarPoint newPoint;
                newPoint.angle = angle;
                newPoint.distance = distance;
                newPoint.angleIsRad = false;
                currentClusterPoints[++currentClusterPointsIndex] = newPoint;
                emptyPoints = 0;
            }
        } else {
            // Si le point lu est valide, on regarde s'il faut le rajouter au cluster
            if (distance > 0.1) {
                PolarPoint lastPoint = currentClusterPoints[currentClusterPointsIndex];
                // Le point lu doit être assez proche du point courant
                if (
                        abs(lastPoint.angle - angle) < maxAngleBetween2PointsDegrees &&
                        abs(lastPoint.distance - distance) < maxDistanceBetween2Pointsmm
                ) {
                    PolarPoint newPoint;
                    newPoint.angle = angle;
                    newPoint.distance = distance;
                    newPoint.angleIsRad = false;
                    currentClusterPoints[++currentClusterPointsIndex] = newPoint;
                    emptyPoints = 0;
                // Si le point lu est trop éloigné, on le zappe et on incrémente le compteur de points ignorés
                } else {
                    emptyPoints++;
                }
            } else {
                emptyPoints++;
            }
            
            // Si on a ignoré trop de points
            if (emptyPoints > maxPointsToSkip) {
                // reset du nombre de points ignorés
                emptyPoints = 0;
                if ((currentClusterPointsIndex+1) >= minPointsPerCluster) {
                    // Si le cluster courant contient assez de points on le rajoute à la liste de clusters
                    clustersCoordinates[clusterCoordinatesWriteCursor++] = computeBarycenterFromPolar(currentClusterPoints, currentClusterPointsIndex+1);
                }
                // On reset le cluster de travail
                currentClusterPointsIndex = -1;
            }
        }

        // Si le cluster courant est plein
        if ((currentClusterPointsIndex+1) >= maxPointsPerCluster) {
            if ((currentClusterPointsIndex+1) >= minPointsPerCluster) {
                // S'il contient assez de points on le rajoute à la liste de clusters
                clustersCoordinates[clusterCoordinatesWriteCursor++] = computeBarycenterFromPolar(currentClusterPoints, currentClusterPointsIndex+1);
            }
            // On reset le cluster de travail
            currentClusterPointsIndex = -1;
        }
        
        // Si on a atteint la limite de cluster à trouver, on arrête
        if ((clusterCoordinatesWriteCursor+1) > maxClusterCount) {
            currentClusterPointsIndex = -1;
            break;
        }
    }
    
    // Si la lecture s'est arrêtée alors qu'on remplissait un cluster, on le rajoute s'il contient le nombre minimum de points requis
    if ((currentClusterPointsIndex+1) >= minPointsPerCluster) {
        clustersCoordinates[clusterCoordinatesWriteCursor++] = computeBarycenterFromPolar(currentClusterPoints, currentClusterPointsIndex+1);
    }
}