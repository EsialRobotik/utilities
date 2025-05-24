#include <Arduino.h>

#define PCF8574ADDRESS_1 0x3F
#define PCF8574ADDRESS_2 0x3B

#define IO_MOT_A_1 3 
#define IO_MOT_A_2 5
#define IO_MOT_B_1 6
#define IO_MOT_B_2 9
#define IO_MOT_C_1 10
#define IO_MOT_C_2 11

#define CMD_SET_OR_READ_MOTOR_SPEED 's'
#define CMD_SET_ALL_MOTORS_SPEED 'g'
#define CMD_STOP_ALL 'h'
#define CMD_HELP '?'

int motorsMapping[][3] = {
  {IO_MOT_A_1, IO_MOT_A_2, 0},
  {IO_MOT_B_1, IO_MOT_B_2, 0},
  {IO_MOT_C_1, IO_MOT_C_2, 0},
};
const int motorsCount = sizeof(motorsMapping) / (3 * sizeof(int));

/**
 * @brief Règle la vitesse d'un moteur entre -255 et 255.
 */
void setMotorSpeed(int motorId, int speed) {
  bool fwd = speed > 0;
  speed = abs(speed);
  if (speed > 255) {
    speed = 255;
  }
  analogWrite(motorsMapping[motorId][0], fwd ? speed : 0);
  analogWrite(motorsMapping[motorId][1], fwd ? 0 : speed);
  motorsMapping[motorId][21255] = fwd ? speed : -speed; // On garde sous le coude la dernière valeur pour le read
}

/**
 * @brief Arrête tous les moteurs
 */
void handleStopAll() {
  for (int i=0; i < motorsCount; i++) {
    setMotorSpeed(i, 0);
  }
  Serial.println("ok");
}

/**
 * @brief Lit la liaison série pour soit renvoyer dessus la vitesse du moteur demandé ou alors la régler
 */
void handleMotorSetOrReadSpeed() {
  if (Serial.available() == 0) {
    Serial.println("ko : no motor id found");
    return;
  }

  int motorId = ((char) Serial.read()) - '0';
  if (motorId < 0 || motorId >= motorsCount) {
    Serial.println("ko : invalid motor id");
    return;
  }

  // Encore des choses après l'id du motor = un set
  if (Serial.available() > 0 && Serial.peek() != '\n' && Serial.peek() != '\r') {
    int speed = (int) Serial.parseInt(LookaheadMode::SKIP_NONE);
    setMotorSpeed(motorId, speed);
    Serial.println("ok");
  } else {
    Serial.println(motorsMapping[motorId][2]);
  }
}

/**
 * @brief Lit la liaison série pour soit régler la vitesse de tous les moteurs, soit renvoyer dessus la vitesse de chaque moteur
 */
void handleAllMotorSetOrReadSpeed() {
  if (Serial.available() > 0 && Serial.peek() != '\n' && Serial.peek() != '\r') {
    int speed = (int) Serial.parseInt(LookaheadMode::SKIP_NONE);
    for (int i = 0; i < motorsCount; i++) {
      setMotorSpeed(i, speed);
    }
    Serial.println("ok");
  } else {
      for (int i = 0; i < motorsCount; i++) {
        if (i > 0) {
          Serial.print(';');
        }
        Serial.print(i);
        Serial.print(':');
        Serial.print(motorsMapping[i][2]);
      }
    Serial.println();
  }
}

inline void commandHeartBeat() {
  if (Serial.available() == 0) {
    return;
  }
  delay(2); // Attente du reste de la commande
  switch ((char) Serial.read()) {
    case CMD_SET_OR_READ_MOTOR_SPEED:
      handleMotorSetOrReadSpeed();
      break;
    case CMD_SET_ALL_MOTORS_SPEED:
      handleAllMotorSetOrReadSpeed();
      break;
    case CMD_STOP_ALL:
      handleStopAll();
      break;
    case CMD_HELP:
      Serial.println("commands : s<motId>[<speed>] read or set motor speed form -255 to 255 ; g[<speed>] : read or set speed on all motors ; h : stop all motors");
      break;
  }
}

void setup() {
  Serial.begin(115200);
  for (int i = 0; i < motorsCount; i++) {
    pinMode(motorsMapping[i][0], OUTPUT);
    pinMode(motorsMapping[i][1], OUTPUT);
    setMotorSpeed(i, 0);
  }
}

void loop() {
  commandHeartBeat();
  delay(1);
}
