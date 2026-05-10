#include <Arduino.h>

 // Vitesse de la liaison série
#define SERIAL_SPEED 115200
// Temps en millisecondes entre 2 print
#define STREAM_PERIOD_MS 100

// La liste des boutons poussoirs et interrupteurs et les broches auquelles ils sont raccordés
#define PIN_BP_1 10
#define PIN_BP_2 13
#define PIN_BP_3 9
#define PIN_BP_4 8
#define PIN_SW_1 12
#define PIN_SW_2 11
#define PIN_SW_3 2
#define PIN_SW_4 3
#define PIN_SW_5 4
#define PIN_SW_6 5
#define PIN_SW_7 7
#define PIN_SW_8 6

const int gpios[] = {
    PIN_BP_1,
    PIN_BP_2,
    PIN_BP_3,
    PIN_BP_4,
    PIN_SW_1,
    PIN_SW_2,
    PIN_SW_3,
    PIN_SW_4,
    PIN_SW_5,
    PIN_SW_6,
    PIN_SW_7,
    PIN_SW_8,  
};

unsigned long lastStream = 0;

void setup() {
    Serial.begin(115200);
    for (int i=0; i < (sizeof(gpios) / sizeof(int)); i++) {
        pinMode(gpios[i], INPUT_PULLUP); // Utilisation des résistances de pull-up internes
    }
}

void loop() {
    if (millis() > lastStream + STREAM_PERIOD_MS) {
        lastStream += STREAM_PERIOD_MS;
        for (int i=0; i < (sizeof(gpios) / sizeof(int)); i++) {
            if (i > 0) {
                Serial.print(';');    
            }
            Serial.print(digitalRead(gpios[i]) ? 0 : 1); // Logic inversée due aux pullups;
        }
        Serial.println();
        Serial.flush();
    }
    delay(1); // économie d'énergie
}
