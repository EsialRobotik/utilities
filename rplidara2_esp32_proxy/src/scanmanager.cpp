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
    } else if (scanMode == SCAN_MODE_CLUSTERING || scanMode == SCAN_MODE_CLUSTERING_ONE_LINE) {
        if (lidar->nextPoint(&angle, &distance)) {
            double degreeAngle = ((double) angle)/RPLIDARA2_UNIT_PER_DEGREE_FLOAT;
            acfd->addPoint(degreeAngle, distance);
            if (clusteringLastExecution == 0 || clusteringLastExecution + clusteringFrequency < millis()) {
                clusteringLastExecution = millis();
                acfd->doClustering();
                bool firstPointWritten = false;
                while (acfd->hasNextClusterCoordinates()) {
                    if (firstPointWritten && scanMode == SCAN_MODE_CLUSTERING_ONE_LINE) {
                        serial->print('#');
                    } else {
                        firstPointWritten = true;
                    }
                    printPoint(acfd->nextClusterCoordinates(), scanMode == SCAN_MODE_CLUSTERING);
                }
                if (scanMode == SCAN_MODE_CLUSTERING_ONE_LINE) {
                    serial->println();
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

void ScanManager::setClusteringPeriod(unsigned long period) {
    clusteringFrequency = period;
}

unsigned long ScanManager::getClusteringPeriod() {
    return clusteringFrequency;
}