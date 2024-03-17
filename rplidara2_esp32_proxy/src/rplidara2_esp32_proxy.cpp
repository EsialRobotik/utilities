#include <Arduino.h>
#include "rplidara2.h"
#include <ACFDImplementation.hpp>
#include "commandmanager.hpp"
#include "motorcontrolmanager.hpp"
#include "scanmanager.hpp"

#define PIN_LIDAR_PWM 18
#define PWM_CHANNEL 0
#define PWM_FREQ 1000
#define PWM_RESOLUTION 10

#define UART_PC_SPEED 250000

MotorControlManager motorControlManager(PIN_LIDAR_PWM, PWM_CHANNEL, PWM_FREQ, PWM_RESOLUTION);
RpLidarA2 lidar(&Serial2, &motorControlManager);
ACFDImplementation acfd(
  /* distance max, on met une valeur très élevée exprès car c'ets géré en amont par le lidar */ 10000.,
  /* min points par cluster */ 5,
  /* max points par cluster */ 30,
  /* max enemis */ 20,
  /* distante max entre 2 points mm */ 50.,
  /* angle max entre 2 pts degrés */ 10.,
  /* max points vides entre 2 points */ 250
);

ScanManager scanManager(&Serial, &lidar, &acfd);
CommandManager commandManager(&Serial, &lidar, &scanManager);

void setup() {
  Serial.begin(UART_PC_SPEED);
  Serial.setTimeout(10);
  motorControlManager.init();
}

void loop() {
  commandManager.heartBeat();
  lidar.scanTick(10);
  scanManager.heartBeat();
}
