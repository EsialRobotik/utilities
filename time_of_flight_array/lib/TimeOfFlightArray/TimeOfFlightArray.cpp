#include "TimeOfFlightArray.hpp"

TimeOfFlightArray::TimeOfFlightArray(unsigned char pcf8574Address, unsigned char firstTimeOfFlightAddress)
: pcf8574Address(pcf8574Address)
, firstTimeOfFlightAddress(firstTimeOfFlightAddress)
{
    pcf8574IsPresent = false;
    for (unsigned short i = 0; i < TOF_MAX_COUNT; i++) {
        tofs[i] = NULL;
        distances[i] = 0xFFFF;
    }
}

void TimeOfFlightArray::init() {
    if (!checkIfPcf8574IsPresent()) {
        return;
    }
    enableAllPcfSlots(false);

    for (int i=0; i<TOF_MAX_COUNT; i++) {
        tofs[i] = instanciateTofIfPresentInSlot(i);
    }
}

bool TimeOfFlightArray::tofExists(unsigned short id) {
    return id < TOF_MAX_COUNT && tofs[id] != NULL;
}

uint16_t TimeOfFlightArray::getDistance(unsigned short id) {
    return id < TOF_MAX_COUNT ? distances[id] : 0xFFFF;
}

void TimeOfFlightArray::enableAllPcfSlots(bool enable) {
  pcf8574_state = enable ? 0b11111111 : 0b00000000;
  Wire.beginTransmission(pcf8574Address);
  Wire.write(pcf8574_state);
  Wire.endTransmission();
}

void TimeOfFlightArray::enableSinglePcfSlot(char slot, bool enable) {
  if (enable) {
    pcf8574_state |= (0b00000001 << slot);
  } else {
    pcf8574_state &= 0b11111111 ^ (0b00000001 << slot);
  }
  Wire.beginTransmission(pcf8574Address);
  Wire.write(pcf8574_state);
  Wire.endTransmission();
}

bool TimeOfFlightArray::checkIfPcf8574IsPresent() {
   Wire.beginTransmission(pcf8574Address);
   pcf8574IsPresent = (Wire.endTransmission() == 0);
   return pcf8574IsPresent;
}

/**
 * @brief Teste la présence du TimeOfLight sur le Bus I2C en activation le slot concerné avant le test
 * 
 * @param slot 
 * @return VL53L1X* 
 */
VL53L1X* TimeOfFlightArray::instanciateTofIfPresentInSlot(int slot) {
    enableSinglePcfSlot(slot, true);
    delay(25); // On laisse le temps en ToF de booter
    Wire.beginTransmission(TOF_DEFAULT_ADDRESS);
    int error = Wire.endTransmission();

    if (error != 0) {
      enableSinglePcfSlot(slot, false);
      return NULL;
    }

    VL53L1X* tof = new VL53L1X();
    tof->setAddress(firstTimeOfFlightAddress + slot + 1);
    return tof;
}

void TimeOfFlightArray::triggerMeasuresBlocking() {
    for (int i=0; i<TOF_MAX_COUNT; i++) {
        if (tofs[i] == NULL) {
        distances[i] = 0xFFFF;
        } else {
        distances[i] = tofs[i]->readSingle(true);
        }
    }
}

unsigned short TimeOfFlightArray::getConnectedTofCount() {
    unsigned short count = 0;
    for (int i=0; i<TOF_MAX_COUNT; i++) {
        if (tofs[i] != NULL) {
            count++;
        }
    }
    return count;
}