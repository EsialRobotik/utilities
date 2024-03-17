#ifndef MOTORCONTROLMANAGER_H_
#define MOTORCONTROLMANAGER_H_

class MotorControlManager {

    public:
        MotorControlManager(int motorPin, int pwmChannel, int pwmFreq, int pwmResolution);
        void init();
        void startRotation();
        void stopRotation();
        bool isRotating();
    private:
        bool isRotatingValue;
        int motorPin;
        int pwmChannel;
        int pwmFreq;
        int pwmResolution;
};

#endif