#include "commandmanager.hpp"

CommandManager::CommandManager(Stream* serial, RpLidarA2* lidar, ScanManager* scanManager)
    : serial(serial)
    , lidar(lidar)
    , scanManager(scanManager)
{
}

void CommandManager::handleCmdRotation() {
  if (serial->available() == 0) {
      serial->println(lidar->isRotating() ? '1' : '0');
  } else {
    switch (serial->read()) {
      case '\n':
      case '\r':
      case '\0':
        serial->println(lidar->isRotating() > 0 ? '1' : '0');
        break;
      case '1':
        lidar->startRotation();
        serial->println("ok");
      break;
      case '0':
        lidar->stopRotation();
        serial->println("ok");
      break;
      default:
        serial->println("err usage : r<rotation status (1 [ON] or 0 [OFF])> / r1 => rotation ON / r0 => rotation OFF");
        break;
    }
  }

}

void CommandManager::handleSerialCommand() {
  if (serial->available() == 0) {
    return;
  }
  delay(5); // on laisse le temps au reste de la commande d'arriver

  char cmd = serial->read();
  if (
    lidar->isScanning() &&
    cmd != CommandManagerCommand::SERIAL_COMMAND_SCAN_STOP &&
    cmd != CommandManagerCommand::SERIAL_COMMAND_RESET
  ) {
    int drop = 16; // On purge par blocs pour pas trop bloquer le scan
    while (serial->available() > 0 && drop > 0) {
      serial->read();
      drop--;
    }
    return;
  }

  switch (cmd) {
    case CommandManagerCommand::SERIAL_COMMAND_SCAN_START:
      lidar->startScan();
      serial->println("start scan");
    break;
    case CommandManagerCommand::SERIAL_COMMAND_MOTOR_ROTATION:
      handleCmdRotation();
    break;
    case CommandManagerCommand::SERIAL_COMMAND_INFORMATION:
      lidar->printInfos();
      serial->println("end infos");
      break;
    case CommandManagerCommand::SERIAL_COMMAND_HEALTH:
      int status;
      int code;
      if (lidar->fetchHealth(&status, &code)) {
        serial->print(status);
        serial->print('/');
        serial->println(code);
      } else {
        serial->println("err fetch health");
      }
    break;
    case CommandManagerCommand::SERIAL_COMMAND_POINT_MAX_DISTANCE:
      handleCmdMaxDistance();
      break;
    case CommandManagerCommand::SERIAL_COMMAND_POINT_MIN_QUALITY:
      handleCmdMinQuality();
      break;
    case CommandManagerCommand::SERIAL_COMMAND_SCAN_STOP:
      lidar->stopScan();
      serial->println("stopscan");
      break;
    case CommandManagerCommand::SERIAL_COMMAND_RESET:
      lidar->reset();
      serial->println("reset");
      break;
    case CommandManagerCommand::SERIAL_COMMAND_SCAN_MODE:
      handleCmdScanMode();
      break;
    case CommandManagerCommand::SERIAL_COMMAND_OUTPUT_FLAVOR:
      handleCmdOutPutFormatFlavor();
      break;
  }
}

void CommandManager::handleCmdMaxDistance() {
  long max = serial->parseInt();
  if (max == 0) {
    serial->println(lidar->getMaxDistance());
  } else {
    lidar->setMaxDistance((unsigned int) max);
    serial->println("ok");
  }
}

void CommandManager::handleCmdMinQuality() {
  long min = serial->parseInt();
  if (min == 0) {
    serial->println(lidar->getMinQuality());
  } else {
    lidar->setMinQuality((unsigned int) min);
    serial->println("ok");
  }
}

void CommandManager::handleCmdScanMode() {
  if (serial->available()) {
    char mode = (char) Serial.read();
    if (mode == 'c') {
        scanManager->setMode(SCAN_MODE_CLUSTERING);
        Serial.println("ok");
    } else if (mode == 'o') {
        scanManager->setMode(SCAN_MODE_CLUSTERING_ONE_LINE);
        Serial.println("ok");
    } else if (mode == 'f') {
        scanManager->setMode(SCAN_MODE_FILTERED);
        Serial.println("ok");
    } else {
        Serial.println("ko");
    }
  } else {
    switch (scanManager->getMode()) {
      case SCAN_MANAGER_SCAN_MODE::SCAN_MODE_CLUSTERING:
        Serial.println('c');
        break;
      case SCAN_MANAGER_SCAN_MODE::SCAN_MODE_CLUSTERING_ONE_LINE:
        Serial.println('o');
        break;
      case SCAN_MANAGER_SCAN_MODE::SCAN_MODE_FILTERED:
        Serial.println('f');
        break;
    }
  }
}

void CommandManager::handleCmdOutPutFormatFlavor() {
  if (serial->available()) {
    char f = serial->read();
    // Coordonnées cartésiennes
    if (f == CommandManagerOutputFlavor::CARTESIAN) {
      scanManager->setOutputFormat(SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_CARTESIAN);
      serial->println("ok");
    // Coordonnées polaires degrés
    } else if (f == CommandManagerOutputFlavor::POLAR_DEGREES) {
      scanManager->setOutputFormat(SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_POLAR_DEGREES);
      serial->println("ok");
    // Coordonnées polaires radian
    } else if (f == CommandManagerOutputFlavor::POLAR_RADIAN) {
      scanManager->setOutputFormat(SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_POLAR_RADIAN);
      serial->println("ok");
    // Format vide/non reocnnu
    } else {
      serial->println("err");
    }
  } else {
    switch (scanManager->getOutputFormat())
    {
      case SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_CARTESIAN:
        serial->println((char) CommandManagerOutputFlavor::CARTESIAN);
      break;
      case SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_POLAR_RADIAN:
        serial->println((char) CommandManagerOutputFlavor::POLAR_RADIAN);
      break;
      case SCAN_MANAGER_OUTPUT_FORMAT::OUTPUT_FORMAT_POLAR_DEGREES:
        serial->println((char) CommandManagerOutputFlavor::POLAR_DEGREES);
      break;
      default:
        serial->println("err");
        break;
    }
  }
}

void CommandManager::heartBeat() {
    handleSerialCommand();
}