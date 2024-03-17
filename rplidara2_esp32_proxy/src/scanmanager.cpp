#include "scanmanager.hpp"

ScanManager::ScanManager(Stream* serial, RpLidarA2* lidar, ACFDImplementation* acfd)
    : serial(serial)
    , lidar(lidar)
    , acfd(acfd)
    , scanMode(SCAN_MODE_FILTERED)
{
}

void ScanManager::heartBeat() {
    if (!lidar->isScanning()) {
        return;
    }
    uint16_t distance;
    uint16_t angle;
    if (scanMode == SCAN_MODE_FILTERED) {
        if (lidar->nextPoint(&angle, &distance)) {
            PolarPoint pp;
            pp.angleIsRad = true;
            pp.angle = angle;
            pp.distance = distance;
            printPoint(polarToCartesian(pp));
        }
    } else if (scanMode == SCAN_MODE_CLUSTERING) {
        if (lidar->nextPoint(&angle, &distance)) {
            double degreeAngle = ((double) angle)/RPLIDARA2_UNIT_PER_DEGREE_FLOAT;
            acfd->addPoint(degreeAngle, distance);
            if (clusteringLastExecution == 0 || clusteringLastExecution + clusteringFrequency < millis()) {
                clusteringLastExecution = millis();
                acfd->doClustering();
                while (acfd->hasNextClusterCoordinates()) {
                    printPoint(acfd->nextClusterCoordinates());
                }
            }
        }
    }
}

SCAN_MANAGER_SCAN_MODE ScanManager::getMode() {
    return scanMode;
}

void ScanManager::setMode(SCAN_MANAGER_SCAN_MODE mode) {
    scanMode = mode;
}

SCAN_MANAGER_OUTPUT_FORMAT ScanManager::getOutputFormat() {
    return outputFormat;
}

void ScanManager::setOutputFormat(SCAN_MANAGER_OUTPUT_FORMAT format) {
    outputFormat = format;
}