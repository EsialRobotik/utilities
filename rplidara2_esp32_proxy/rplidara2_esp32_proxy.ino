#include "rplidara2.h"

#define PIN_LIDAR_PWM 18
#define PWM_CHANNEL 0
#define PWM_FREQ 1000
#define PWM_RESOLUTION 10

#define UART_PC_SPEED 250000

bool global_motor_spinning = false;
bool global_motor_spinning_previous = 0;

RpLidarA2 lidar(&Serial2, &global_motor_spinning);

/**
 * @brief Gère le démarrage/l'arrêt de la rotation du lidar
 */
void handleCmdRotation() {
  if (Serial.available() == 0) {
      Serial.println(lidar.isRotating() ? '1' : '0');
  } else {
    switch (Serial.read()) {
      case '\n':
      case '\r':
      case '\0':
        Serial.println(lidar.isRotating() > 0 ? '1' : '0');
        break;
      case '1':
        lidar.startRotation();
        Serial.println("ok");
      break;
      case '0':
        lidar.stopRotation();
        Serial.println("ok");
      break;
      default:
        Serial.println("err usage : r => rotation status (1 [ON] or 0 [OFF]) / r1 => rotation ON / r0 => rotation OFF");
    }
  }
}

/**
 * @brief Gère la lecture/l'écriture de la distance max
 */
void handleCmdMaxDistance() {
  long max = Serial.parseInt();
  if (max == 0) {
    Serial.println(lidar.getMaxDistance());
  } else {
    lidar.setMaxDistance((unsigned int) max);
    Serial.println("ok");
  }
}

/**
 * @brief Gère la lecture/l'écriture de la qualité minimum attendu pour chauqe point du lidar
 */
void handleCmdMinQuality() {
  long min = Serial.parseInt();
  if (min == 0) {
    Serial.println(lidar.getMinQuality());
  } else {
    lidar.setMinQuality((unsigned int) min);
    Serial.println("ok");
  }
}

/**
 * @brief Analyse et traite les commandes qui arrivent sur le port série USB
 */
void handleSerialCommand() {
  if (Serial.available() == 0) {
    return;
  }
  delay(5); // on laisse le temps au reste de la commande d'arriver

  char cmd = Serial.read();
  if (lidar.isScanning() && cmd != 'h') {
    int drop = 16; // On purge par blocs pour pas trop bloquer le scan
    while (Serial.available() > 0 && drop > 0) {
      Serial.read();
      drop--;
    }
    return;
  }

  switch (cmd) {
    case 's':
      lidar.startScan();
      Serial.println("start scan");
    break;
    case 'r':
      handleCmdRotation();
    break;
    case 'i':
      lidar.printInfos();
      Serial.println("end infos");
      break;
    case 'l':
      int status;
      int code;
      if (lidar.fetchHealth(&status, &code)) {
        Serial.print(status);
        Serial.print('/');
        Serial.println(code);
      } else {
        Serial.println("err fetch health");
      }
    break;
    case 'd':
      handleCmdMaxDistance();
      break;
    case 'q':
      handleCmdMinQuality();
      break;
    case 'h':
      lidar.stopScan();
      Serial.println("stopscan");
      break;
    case 'e':
      lidar.reset();
      Serial.println("reset");
      break;
  }
}

/**
 * @brief Gère le moteur du lidar
 */
void handleMotorControl() {
  if (global_motor_spinning_previous == global_motor_spinning) {
    return;
  }
  global_motor_spinning_previous = global_motor_spinning;
  ledcWrite(PWM_CHANNEL, global_motor_spinning ? (int)(pow(2, PWM_RESOLUTION) - 1) : 0);
}

/**
 * @brief Gère la lecture du lidar
 */
void handleScan() {
  if (lidar.isScanning()) {
    lidar.scanTick();
    uint16_t distance;
    uint16_t angle;
    if (lidar.nextPoint(&angle, &distance)) {
      Serial.print(((float)angle)/RPLIDARA2_UNIT_PER_DEGREE_FLOAT);
      Serial.print(';');
      Serial.println(distance);
    }
  }
}

void setup() {
  Serial.begin(UART_PC_SPEED);
  Serial.setTimeout(10);
  ledcSetup(PWM_CHANNEL, PWM_FREQ, PWM_RESOLUTION);
  ledcAttachPin(PIN_LIDAR_PWM, PWM_CHANNEL);
}

void loop() {
  handleSerialCommand();
  handleMotorControl();
  handleScan();
}
