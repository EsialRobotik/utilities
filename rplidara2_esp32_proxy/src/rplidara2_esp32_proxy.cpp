#include <Arduino.h>
#include "rplidara2.h"
#include <ACFDImplementation.hpp>

#define PIN_LIDAR_PWM 18
#define PWM_CHANNEL 0
#define PWM_FREQ 1000
#define PWM_RESOLUTION 10

#define UART_PC_SPEED 250000

// Les commandes disponibles sur la liaison série
#define SERIAL_COMMAND_SCAN_START 's'
#define SERIAL_COMMAND_SCAN_STOP 'h'
#define SERIAL_COMMAND_SCAN_MODE 'm'
#define SERIAL_COMMAND_MOTOR_ROTATION 'r'
#define SERIAL_COMMAND_INFORMATION 'i'
#define SERIAL_COMMAND_RESET 'e'
#define SERIAL_COMMAND_POINT_MIN_QUALITY 'q'
#define SERIAL_COMMAND_POINT_MAX_DISTANCE 'd'
#define SERIAL_COMMAND_HEALTH 'l'

/**
 * @brief Modes de scan disponibles 
 */
enum SCAN_MODE {
  SCAN_MODE_FILTERED, // Filtré = les données du lidar qui respectent les filtres de distance et de qualité sont renvoyées
  SCAN_MODE_CLUSTERING, // Clustering = les données du lidar sont analysées pour en extraire les groupes de points trouvés et en renvoyer leur barycentre
};

bool global_motor_spinning = false;
bool global_motor_spinning_previous = 0;
SCAN_MODE global_scan_mode = SCAN_MODE_CLUSTERING;
unsigned long global_clustering_last_execution = 0; // timestamp du dernier clustering effectué
unsigned long global_clustering_frequency = 200; // période entre 2 exécutions du clustering en ms

RpLidarA2 lidar(&Serial2, &global_motor_spinning);
ACFDImplementation acfd(
  /* distance max */ 1500.,
  /* min points par cluster */ 5,
  /* max points par cluster */ 30,
  /* max enemis */ 20,
  /* distante max entre 2 points mm */ 50.,
  /* angle max entre 2 pts degrés */ 10.,
  /* max points vides entre 2 points */ 250
);

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
 * @brief Gère la lecture/l'écriture de la qualité minimum attendu pour chaque point du lidar
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
 * @brief Gère le paramétrage du mode de scan :
 * c = SCAN_MODE_CLUSTERING
 * f = SCAN_MODE_FILTERED
 */
void handleCmdScanMode() {
  if (Serial.available()) {
    char mode = (char) Serial.read();
    if (mode == 'c') {
      global_scan_mode = SCAN_MODE_CLUSTERING;
    } else if (mode == 'f') {
      global_scan_mode = SCAN_MODE_FILTERED;
    }
    Serial.println("ok");
  } else {
    Serial.println(global_scan_mode == SCAN_MODE_CLUSTERING ? "clustering" : "filtered");
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
    case SERIAL_COMMAND_SCAN_START:
      lidar.startScan();
      Serial.println("start scan");
    break;
    case SERIAL_COMMAND_MOTOR_ROTATION:
      handleCmdRotation();
    break;
    case SERIAL_COMMAND_INFORMATION:
      lidar.printInfos();
      Serial.println("end infos");
      break;
    case SERIAL_COMMAND_HEALTH:
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
    case SERIAL_COMMAND_POINT_MAX_DISTANCE:
      handleCmdMaxDistance();
      break;
    case SERIAL_COMMAND_POINT_MIN_QUALITY:
      handleCmdMinQuality();
      break;
    case SERIAL_COMMAND_SCAN_STOP:
      lidar.stopScan();
      Serial.println("stopscan");
      break;
    case SERIAL_COMMAND_RESET:
      lidar.reset();
      Serial.println("reset");
      break;
    case SERIAL_COMMAND_SCAN_MODE:
      handleCmdScanMode();
      break;
  }
}

/**
 * @brief Gère la commande moteur PWM du lidar
 */
void handleMotorControl() {
  if (global_motor_spinning_previous == global_motor_spinning) {
    return;
  }
  global_motor_spinning_previous = global_motor_spinning;
  ledcWrite(PWM_CHANNEL, global_motor_spinning ? (int)(pow(2, PWM_RESOLUTION) - 1) : 0);
}

/**
 * @brief Gère le calcul des données du scan à renvoyer sur la liaison série
 */
void handleScanOutput() {
  if (!lidar.isScanning()) {
    return;
  }
  uint16_t distance;
  uint16_t angle;
  if (global_scan_mode == SCAN_MODE_FILTERED) {
    if (lidar.nextPoint(&angle, &distance)) {
      Serial.print(((float)angle)/RPLIDARA2_UNIT_PER_DEGREE_FLOAT);
      Serial.print(';');
      Serial.println(distance);
    }
  } else if (global_scan_mode == SCAN_MODE_CLUSTERING) {
    if (lidar.nextPoint(&angle, &distance)) {
      double degreeAngle = ((double) angle)/RPLIDARA2_UNIT_PER_DEGREE_FLOAT;
      acfd.addPoint(degreeAngle, distance);
      if (global_clustering_last_execution == 0 || global_clustering_last_execution + global_clustering_frequency < millis()) {
        global_clustering_last_execution = millis();
        Serial.println("doclustering");
        acfd.doClustering();
        Serial.println("printresult");
        while (acfd.hasNextClusterCoordinates()) {
          Point p = acfd.nextClusterCoordinates();
          PolarPoint pp = cartesianToPolar(p, false);
          Serial.print(pp.angle);
          Serial.print(';');
          Serial.println(pp.distance);
        }
      }
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
  lidar.scanTick(10);
  handleScanOutput();
}
