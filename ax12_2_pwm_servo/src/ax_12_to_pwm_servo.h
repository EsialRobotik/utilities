#ifndef _AX_12_TO_PWM_SERVO
#define _AX_12_TO_PWM_SERVO

#include <arduino.h>
#include <ESP32Servo.h>
#include "ax_12_instruction_listener.h"

/// @brief Permet de lier la commande d'un servomoteur AX12 sur une commande de servomoteur PWM
class Ax12ToPwmServo {
    public:
        /// @brief 
        /// @param instructionListener Référence à l'écouteur d'instructions ax12
        /// @param responseSerial Référence à la liaison série à utiliser pour l'envoi de paquets de réponse
        /// @param ax12Id ID de l'AX12 à écouter
        /// @param servoGpio GPIO à utiliser pour piloter le servomoteur
        Ax12ToPwmServo(Ax12InstructionListener* instructionListener, HardwareSerial* responseSerial, int ax12Id, int servoGpio);

        /// @brief Initialise le liaen ax12 vers servo pwm. A appeler une seule fois avant d'appeler les autres méthodes.
        void init();

        /// @brief Méthode à appeler périodiquement pour piloter le servomoteur
        void heartBeat();

        /// @brief Renvoie la position courante du servo
        int currentPosition();
    private:
        /// @brief ID de l'AX12 à écouter
        int ax12Id;

        /// @brief GPIO à utiliser pour piloter le servomoteur
        int servoGpio;

        /// @brief Référence à l'écouteur d'instructions ax12
        Ax12InstructionListener* instructionListener;

        /// @brief Référence à la liaison série à utiliser pour l'envoi des paquets de réponse
        HardwareSerial* responseSerial;

        /// @brief Référence au servo à piloter
        Servo servo;

        /// @brief Date estimée de fin de rotation du servo en millisecondes
        unsigned long lastTargetReachTimestamp;

        /// @brief Envoie une réponse sur la liaison série
        /// @param error 
        /// @param params 
        /// @param params_length 
        void sendResponse(unsigned char error, unsigned char * params, int params_length);
};

#endif // _AX_12_TO_PWM_SERVO