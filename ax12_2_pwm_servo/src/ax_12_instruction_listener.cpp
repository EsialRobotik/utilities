#include "ax_12_instruction_listener.h"

Ax12InstructionListener::Ax12InstructionListener(HardwareSerial *hwSerial)
  : hwSerial(hwSerial)
{
}

bool Ax12InstructionListener::heartBeat()
{
  // CF https://emanual.robotis.com/docs/en/dxl/ax/ax-12a/
  // Instruction packet : FF FF <ID> <LEN> <INSTR> <PAR0>..<PARN> <CKSUM>
  if(this->hwSerial->available() < 6) {
    this->lastInstruction.error = Ax12InstructionDataError::BAD_INITIAL_DATA_LENGTH;
    this->flushSerialInput();
    return false;
  }
  if (this->hwSerial->read() != 0xFF) {
    this->lastInstruction.error = Ax12InstructionDataError::BAD_FIRST_BYTE;
    this->flushSerialInput();
    return false;
  }
  if (this->hwSerial->read() != 0xFF) {
    this->lastInstruction.error = Ax12InstructionDataError::BAD_SECOND_BYTE;
    this->flushSerialInput();
    return false;
  }

  byte addr = this->hwSerial->read();
  byte len = this->hwSerial->read();
  if (len < 2 || this->hwSerial->available() < (len - 2)) {
    this->lastInstruction.error = Ax12InstructionDataError::BAD_INITIAL_DATA_LENGTH;
    this->flushSerialInput();
    return false;
  }
  AX12Command instr = (AX12Command) this->hwSerial->read();
  byte localChecksum = addr;
  localChecksum += len;
  localChecksum += instr;
  byte data[2];
  for (byte i=0; i<len-2; i++) {
    localChecksum += (data[i] = this->hwSerial->read());
  }
  localChecksum = (~localChecksum) & 0xFF;
  byte receivedCheckSum;
  this->hwSerial->readBytes(&receivedCheckSum, 1);
  if (localChecksum != receivedCheckSum) {
    this->flushSerialInput();
    this->lastInstruction.error = Ax12InstructionDataError::BAD_CHECKSUM;
    return false;
  }

  this->lastInstruction = {Ax12InstructionDataError::NO_ERROR, addr, instr, data[0], data[1], data[2]};
  return true;
}

Ax12InstructionData Ax12InstructionListener::getLastInstruction() {
  return this->lastInstruction;
}

inline void Ax12InstructionListener::flushSerialInput() {
  while (this->hwSerial->available() > 0) {
    this->hwSerial->read();
  }
}