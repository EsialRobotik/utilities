#include "motorcontrolmanager.hpp"
#include <Arduino.h>

MotorControlManager::MotorControlManager(int motorPin, int pwmChannel, int pwmFreq, int pwmResolution)
    : motorPin(motorPin)
    , pwmChannel(pwmChannel)
    , pwmFreq(pwmFreq)
    , pwmResolution(pwmResolution)
{
}

void MotorControlManager::init()
{
  pinMode(motorPin, OUTPUT);
  ledcSetup(pwmChannel, pwmFreq, pwmResolution);
  ledcAttachPin(motorPin, pwmChannel);
  stopRotation();
}

bool MotorControlManager::isRotating() {
    return this->isRotatingValue;
}

void MotorControlManager::startRotation() {
    isRotatingValue = true;
    ledcWrite(pwmChannel, (int)(pow(2, pwmResolution) - 1));
}

void MotorControlManager::stopRotation() {
    isRotatingValue = false;
    ledcWrite(pwmChannel, 0);
}