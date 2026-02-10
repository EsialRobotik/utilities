#ifndef _AX_12_ENUMS
#define _AX_12_ENUMS

// CF https://emanual.robotis.com/docs/en/dxl/ax/ax-12a/

// Registres disponibles sur un AX12
enum AX12Registers {
  AX12_ROM_MODEL_NUMBER = 0x00,
  AX12_ROM_FIRMWARE_VERSION = 0x02,
  AX12_ROM_ID = 0x03,
  AX12_ROM_BAUD_RATE = 0x04,
  AX12_ROM_RETURN_DELAY_TIME = 0x05,
  AX12_ROM_CW_ANGLE_LIMIT = 0x06,
  AX12_ROM_CCW_ANGLE_LIMIT = 0x08,
  AX12_ROM_HIGH_TEMP_LIMIT = 0x0B,
  AX12_ROM_LOW_VOLTAGE_LIMIT = 0x0C,
  AX12_ROM_HIGH_VOLTAGE_LIMIT = 0x0D,
  AX12_ROM_MAX_TORQUE = 0x0E,
  AX12_ROM_STATUS_RETURN_LEVEL = 0x10,
  AX12_ROM_ALARM_LED = 0x11,
  AX12_ROM_ALARM_SHUTDOWN = 0x12,
  AX12_ROM_DOWN_CALIBRATION = 0x14,
  AX12_ROM_UP_CALIBRATION = 0x16,
  AX12_RAM_TORQUE_ENABLE = 0x18,
  AX12_RAM_LED = 0x19,
  AX12_RAM_CW_COMPILANCE_MARGIN = 0x1A,
  AX12_RAM_CCW_COMPILANCE_MARGIN = 0x1B,
  AX12_RAM_CW_COMPILANCE_SLOPE = 0x1C,
  AX12_RAM_CCW_COMPILANCE_SLOPE = 0x1D,
  AX12_RAM_GOAL_POSITION = 0x1E,
  AX12_RAM_MOVING_SPEED = 0x20,
  AX12_RAM_TORQUE_LIMIT = 0x22,
  AX12_RAM_PRESENT_POSITION = 0x24,
  AX12_RAM_PRESENT_SPEED = 0x26,
  AX12_RAM_PRESENT_LOAD = 0x28,
  AX12_RAM_PRESENT_VOLTAGE = 0x2A,
  AX12_RAM_PRESENT_TEMPERATURE = 0x2B,
  AX12_RAM_REGISTERED_INSTRUCTION = 0x2C,
  AX12_RAM_MOVING = 0x2E,
  AX12_RAM_LOCK = 0x2F,
  AX12_RAM_PUNCH = 0x30,
};

/// @brief Les différentes erreurs possible qu'un AX12 peut renvoyer
enum AX12ErrorFlags {
    INPUT_VOLTAGE_ERROR = 0x01,
    ANGLE_LIMIT_ERROR = 0x02,
    OVERHEATING_ERROR = 0x04,
    RANGE_ERROR = 0x08,
    CHECKSUM_ERROR = 0x10,
    OVERLOAD_ERROR = 0x20,
    INSTRUCTION_ERROR = 0x40,
};

/// @brief Représente les différentes commandes qu'un AX12 peut gérer
enum AX12Command {
  AX12_INSTR_PING = 0x01,
  AX12_INSTR_READ_DATA = 0x02,
  AX12_INSTR_WRITE_DATA = 0x03,
  AX12_INSTR_RESET = 0x06,
};

/// @brief Liste des raisons possibles menant à une Ax12InstructionData inutilisable
enum Ax12InstructionDataError {
  NO_ERROR,
  BAD_INITIAL_DATA_LENGTH,
  BAD_FIRST_BYTE,
  BAD_SECOND_BYTE,
  BAD_CHECKSUM,
};

/// @brief Représente une instruction AX12
struct Ax12InstructionData {
  Ax12InstructionDataError error;
  byte addr;
  AX12Command instr;
  byte reg;
  byte data1;
  byte data2;

  unsigned short dataToUShort() {
    return (data2 << 8) | data1;
  };

  bool operator==(Ax12InstructionData b) {
    return error == b.error && addr == b.addr && instr == b.instr && reg == b.reg && data1 == b.data1 && data2 == b.data2;
  }
  bool operator!=(Ax12InstructionData b) {
    return !(*this == b);
  }
};

#endif // _AX_12_ENUMS


