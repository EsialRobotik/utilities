#include <Arduino.h>
#include <Wire.h>
#include <VL53L1X.h>
#include <TimeOfFlightArray.hpp>

#define PCF8574ADDRESS_1 0x3F
#define PCF8574ADDRESS_2 0x3B

TimeOfFlightArray tofArray1 = TimeOfFlightArray(PCF8574ADDRESS_1);
TimeOfFlightArray tofArray2 = TimeOfFlightArray(PCF8574ADDRESS_2, TOF_DEFAULT_ADDRESS + 0x08);

/**
 * @brief écrit sur la liaison série la valeur de toutes les mesures lues par @see triggerMeasure()
 */
void writeMeasuresOnSerial(TimeOfFlightArray *tof) {
  for (int i=0; i<TOF_MAX_COUNT; i++) {
    uint16_t d = tof->getDistance(i);
    if (i>0) {
      Serial.print(";");
    }
    if (d == 0xFFFF) {
      Serial.print("-      ");
    } else {
      Serial.print(d);
      if (d < 10) {
        Serial.print("     ");
      } else if (d < 100) {
        Serial.print("    ");
      } else if (d < 1000) {
        Serial.print("   ");
      } else if (d < 10000) {
        Serial.print("  ");
      } else if (d < 100000) {
        Serial.print(" ");
      }
    }
  }
}

void setup() {
  Wire.begin();
  Serial.begin(115200);

  Serial.println("Scanning I2C address...");
  for (int i=0; i<127; i++) {
    Wire.beginTransmission(i);
    if (Wire.endTransmission() == 0) {
      Serial.print("Device found at 0x");
      Serial.println(i, HEX);
    }
  }

  tofArray1.init();
  tofArray2.init();

  if (!tofArray1.checkIfPcf8574IsPresent()) {
    Serial.println("PCF8574 n1 non joignable. Arret du programme.");
    while (true) {
      delay(10);
    }
  }

  if (!tofArray2.checkIfPcf8574IsPresent()) {
    Serial.println("PCF8574 n1 non joignable. Arret du programme.");
    while (true) {
      delay(10);
    }
  }

  tofArray1.startContinuous(20);
  tofArray2.startContinuous(20);

  int connectedTofs = tofArray1.getConnectedTofCount() + tofArray2.getConnectedTofCount();
  Serial.print(connectedTofs);
  Serial.println(" tof(s) connecte(s)");
}

void loop() {
  tofArray1.triggerMeasuresNonBlocking();
  tofArray2.triggerMeasuresNonBlocking();
  writeMeasuresOnSerial(&tofArray1);
  Serial.print(";");
  writeMeasuresOnSerial(&tofArray2);
  Serial.println();
  delay(20);
}
