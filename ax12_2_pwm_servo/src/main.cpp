#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include <ESP32Servo.h>
#include "ax_12_instruction_listener.h"
#include "ax_12_to_pwm_servo.h"

// Led embarquée sur la carte de développement ESP32 C3 ZERO
#define LED_PIN GPIO_NUM_10
#define LED_COUNT 1
#define BRIGHTNESS 32
#define LED_TYPE WS2812
#define COLOR_ORDER GRB

// Adresses "AX12" des 4 servomoteurs à piloter
#define AX2_ADDR_SERVO_1 20
#define AX2_ADDR_SERVO_2 AX2_ADDR_SERVO_1 + 1
#define AX2_ADDR_SERVO_3 AX2_ADDR_SERVO_1 + 2
#define AX2_ADDR_SERVO_4 AX2_ADDR_SERVO_1 + 3

// GPIO des 4 servomoteurs à piloter
#define PIN_SERVO_1 GPIO_NUM_0
#define PIN_SERVO_2 GPIO_NUM_1
#define PIN_SERVO_3 GPIO_NUM_2
#define PIN_SERVO_4 GPIO_NUM_3

// GPIO de la liaison série AX12
#define UART_AX12_RX GPIO_NUM_8
#define UART_AX12_TX GPIO_NUM_9

Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRBW + NEO_KHZ800);
Ax12InstructionListener ax12InstructionListener(&Serial1);
Ax12ToPwmServo ax12ToServo1(&ax12InstructionListener, &Serial1, AX2_ADDR_SERVO_1, PIN_SERVO_1);
Ax12ToPwmServo ax12ToServo2(&ax12InstructionListener, &Serial1, AX2_ADDR_SERVO_2, PIN_SERVO_2);
Ax12ToPwmServo ax12ToServo3(&ax12InstructionListener, &Serial1, AX2_ADDR_SERVO_3, PIN_SERVO_3);
Ax12ToPwmServo ax12ToServo4(&ax12InstructionListener, &Serial1, AX2_ADDR_SERVO_4, PIN_SERVO_4);

bool ledToggle = false; // sert à faire une bascule entre deux couleurs sur la led pour indiquer que le pgramme fonctionne (= heartbeat)
unsigned long lastHeartBeatTime = 0; // timestamp qui marque la dernière pulsation du heartbeat
unsigned long nextIdleTime = 0; // timestamp de repère pour basculer la couleur de la led entre "en activité" et "en attente"
uint32_t lastColor = 0; // Dernière couleur envoyée à la LED pour éviter de l'appeler quand la couleur n'a pas changé

void setup() {
    Serial.begin(115200);
    Serial1.begin(115200, SERIAL_8N1, UART_AX12_RX, UART_AX12_TX);
    ax12ToServo1.init();
    ax12ToServo2.init();
    ax12ToServo3.init();
    ax12ToServo4.init();
    strip.begin();
    strip.setBrightness(BRIGHTNESS);
    strip.show();
}

void loop() {
    // Gestion d'un heart beat pour confirmer que le programme est vivant
    if (millis() + 5000 > lastHeartBeatTime) {
        ledToggle = !ledToggle;
        lastHeartBeatTime += 5000;
    }

    // Par défaut c'est la couleur "travail en cours" qui est paramétrée
    uint32_t currentColor = strip.Color(255, 0, 255);

    // Si une instruction valide est trouvée
    if (ax12InstructionListener.heartBeat()) {
        Ax12InstructionData instr = ax12InstructionListener.getLastInstruction();
        // On réveille tous les servos pour qu'ils géère l'instruction si elle leur est déstinée
        ax12ToServo1.heartBeat();
        ax12ToServo2.heartBeat();
        ax12ToServo3.heartBeat();
        ax12ToServo4.heartBeat();
        nextIdleTime = millis() + 100;
    // Pas d'instruction, alors on gère juste le heartbeat
    } else if (millis() > nextIdleTime) {
        currentColor = ledToggle ? strip.Color(0, 255, 0) : strip.Color(255, 0, 0);
    }

    // Mise à jour de la LED si la couleur a changé
    if (currentColor != lastColor) {
        lastColor = currentColor;
        strip.setPixelColor(0, lastColor);
        strip.show();
    }

    // Pour économiser de l'énergie
    delay(1);
}
