#include "rplidara2.h"

RpLidarA2::RpLidarA2(HardwareSerial *serial, MotorControlManager* motorControlManager)
    : serial(serial)
    , motorControlManager(motorControlManager)
{
    this->simpleScanRunning = false;
    this->bufferReadPosition = 0;
    this->stopRotation();
    this->minQualityThreshold = 0;
    this->maxRawDistance = RPLIDARA2_MAX_DISTANCE_UNIT_EXCLUDED;
    serial->begin(RPLIDARA2_UART_SPEED);
    for (int i=0; i<RPLIDARA2_TOTAL_ANGLE_UNITS; i++) {
        this->raw_distance_buffer[i] = 0;
    }
}

void RpLidarA2::flushSerialIn() {
    while (this->serial->available() > 0) {
        this->serial->read();
    }
}

void RpLidarA2::sendCmd(char command, char payloadSize, char *payloadData) {
    this->flushSerialIn();
    char checksum = RPLIDARA2_FLAG_START_1 ^ command ^ payloadSize;

    this->serial->write(RPLIDARA2_FLAG_START_1);
    this->serial->write(command);

    if (payloadSize > 0) {
        this->serial->write(payloadSize);
        for (int i=0; i<payloadSize; i++) {
            this->serial->write(payloadData[i]);
            checksum ^= payloadData[i];
        }
        this->serial->write(checksum);
    }
}

bool RpLidarA2::readSingleResponse(char *response, char responseSize) {
    int i = 0;
    int retry = 10;

    while (this->serial->available() < 7 && retry > 0) {
        retry--;
        delay(1);
    }
    if (retry < 0) {
        return false;
    }
    if (this->serial->read() != RPLIDARA2_FLAG_START_1) {
            return false;
    }
    if (this->serial->read() != RPLIDARA2_FLAG_START_2) {
        return false;
    }
    for (int j=0; j<5; j++) {
        this->serial->read();
    }

    while (i<responseSize && retry > 0) {
         if (this->serial->available() < 1) {
            retry--;
            delay(1);
            continue;
        }
        response[i] = this->serial->read();
        i++;
    }

    return retry > 0;
}

void RpLidarA2::printInfos() {
    if (this->isScanning()) {
        return;
    }
    char infos[21];
    infos[20] = '\0';
    this->sendCmd(RPLIDARA2_COMMAND_GET_INFO, 0, 0);
    if (this->readSingleResponse(infos, 20)) {
        Serial.print("model: 0x");
        Serial.println(infos[0], HEX);
        Serial.print("firmware: 0x");
        Serial.print(infos[2], HEX);
        Serial.println(infos[1], HEX);
        Serial.print("hardware: 0x");
        Serial.println(infos[3], HEX);
        Serial.print("S/N: 0x");
        for (int i=4; i<20; i++) {
            Serial.print(infos[i], HEX);
        }
        Serial.println();
    } else {
        Serial.println("err get info");
    }
}

bool RpLidarA2::fetchHealth(int *status, int *errorCode) {
    if (this->isScanning()) {
        return false;
    }
    this->sendCmd(RPLIDARA2_COMMAND_GET_HEALTH, 0, 0);
    char infos[3];
    if (this->readSingleResponse(infos, 3)) {
        *status = 0x00 | infos[0];
        *errorCode = ((0x00 | infos[1]) << 8) | infos[2];
        return true;
    } else {
        return false;
    }
}

bool RpLidarA2::startScan() {
    if (this->simpleScanRunning) {
        return true;
    }
    this->startRotation();
    this->sendCmd(RPLIDARA2_COMMAND_SCAN, 0, 0);
    this->simpleScanRunning = this->readSingleResponse(0, 0);
    return this->simpleScanRunning;
}

bool RpLidarA2::isScanning() {
    return this->simpleScanRunning;
}

void RpLidarA2::startRotation() {
    motorControlManager->startRotation();
}

void RpLidarA2::stopRotation() {
    motorControlManager->stopRotation();
}

bool RpLidarA2::isRotating() {
    return motorControlManager->isRotating();
}

bool RpLidarA2::scanTick(int bucket) {
    if (this->simpleScanRunning) {
        bool err = false;
        while (bucket > 0) {
            bucket--;

            if (this->serial->available() > 5) {
            uint8_t quality = this->serial->read();
            uint8_t angleLow = this->serial->read();
            uint8_t angleHigh = this->serial->read();
            uint8_t distanceLow = this->serial->read();
            uint8_t distanceHigh = this->serial->read();

            // Contrôle de S et !S
            if ((quality & 0x01) == ((quality >> 1) & 0x01)) {
                err = true;
            }
            quality = quality >> 2;

            // Contrôle de la constante C
            if (!(angleLow & 0x01)) {
                err = true;
            }

            uint16_t angleUnit = ((angleHigh << 8) | angleLow) >> 1;
            uint16_t rawDistance = ((distanceHigh << 8) | distanceLow);
            // Contrôle de l'angle max et de la distance
            if (angleUnit > RPLIDARA2_TOTAL_ANGLE_UNITS || rawDistance == 0) {
                err = true;
            }

            // On garde un point s'il est valide, qu'il n'est pas trop loin et qu'il a une bonne qualité
            if (!err && rawDistance <= this->maxRawDistance && quality >= this->minQualityThreshold) {
                this->raw_distance_buffer[angleUnit] = rawDistance;
            }
        }

        // Les erreurs peuvent être dues à un décalage dans la lecture parce qu'on est trop lent à dépiler
        // dans ce cas on fait glisser de déclage pour qu'il se stabilise de lui même
        if (err && this->serial->available() > 0) {
            this->serial->read();
        }
        }
        return true;
    }
    return false;
}

void RpLidarA2::stopScan() {
    this->stopRotation();
    this->sendCmd(RPLIDARA2_COMMAND_STOP, 0, 0);
    this->simpleScanRunning = false;
    this->flushSerialIn();
}

void RpLidarA2::reset() {
    this->stopRotation();
    this->sendCmd(RPLIDARA2_COMMAND_RESET, 0, 0);
    this->simpleScanRunning = false;
    this->flushSerialIn();
}

bool RpLidarA2::nextPoint(uint16_t *rawangle, uint16_t *distance) {
    // On cherche une valeur dans les X prochaines cases
    int triesRemaining = 16;
    while (triesRemaining > 0) {
        this->bufferReadPosition++;
        if (this->bufferReadPosition >= RPLIDARA2_TOTAL_ANGLE_UNITS) {
            this->bufferReadPosition = 0;
        }
        // Si la case contient une valeur valide
        if (this->raw_distance_buffer[this->bufferReadPosition] > 0) {
            // Distance en mm = valeur brute / unité par millimètres
            *distance = this->raw_distance_buffer[this->bufferReadPosition] / RPLIDARA2_UNIT_PER_MM;
            // On supprime la valeur lue du buffer
            this->raw_distance_buffer[this->bufferReadPosition] = 0;
            *rawangle = this->bufferReadPosition;
            return true;
        }
        triesRemaining--;
    }
    return false;
}

void RpLidarA2::setMinQuality(uint8_t min) {
    this->minQualityThreshold = min;
}

uint8_t RpLidarA2::getMinQuality() {
    return this->minQualityThreshold;
}

void RpLidarA2::setMaxDistance(uint16_t max) {
    this->maxRawDistance = max * RPLIDARA2_UNIT_PER_MM;
}

uint16_t RpLidarA2::getMaxDistance() {
    return this->maxRawDistance / RPLIDARA2_UNIT_PER_MM;
}