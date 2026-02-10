#include "ax_12_to_pwm_servo.h"

Ax12ToPwmServo::Ax12ToPwmServo(Ax12InstructionListener* instructionListener, HardwareSerial* responseSerial, int ax12Id, int servoGpio)
: instructionListener(instructionListener)
, responseSerial(responseSerial)
, ax12Id(ax12Id)
, servoGpio(servoGpio)
, lastTargetReachTimestamp(0)
{
}

void Ax12ToPwmServo::init()
{
    servo.attach(servoGpio);
    servo.write(0);
}

void Ax12ToPwmServo::heartBeat() {
    Ax12InstructionData instr = this->instructionListener->getLastInstruction();
    // Si l'instruction courante n'est pas valide ou ne nous concerne pas, on ignore
    if (instr.error != Ax12InstructionDataError::NO_ERROR || instr.addr != this->ax12Id) {
        return;
    }

    if (instr.instr == AX12Command::AX12_INSTR_PING) {
        this->sendResponse(0x00, NULL, 0);
        return;
    } else if (instr.instr == AX12Command::AX12_INSTR_WRITE_DATA) {
        switch (instr.reg) {
            case AX12Registers::AX12_RAM_GOAL_POSITION:
                float rawGoal = (float) instr.dataToUShort();
                int goal = (int) ((rawGoal / 1023.) * 180.);
                int previousGoal = this->servo.read();
                this->servo.write(goal);
                // On prend une vitesse de rotation des servos de 20ms par degré pour calculer la date de fin de rotation
                this->lastTargetReachTimestamp = millis() + (20 * abs(previousGoal - goal));
                this->sendResponse(0, NULL, 0);
                return;
        }
    } else if (instr.instr == AX12Command::AX12_INSTR_READ_DATA) {
        switch (instr.reg) {
            case AX12Registers::AX12_RAM_MOVING:
            {
                unsigned char rotating = millis() >= this->lastTargetReachTimestamp ? 0x00 : 0x01;
                this->sendResponse(0, &rotating, 1);
                return;
            }
            case AX12Registers::AX12_RAM_PRESENT_POSITION:
            {
                unsigned char position[2];
                unsigned short pos = (unsigned short) ((((float)this->servo.read()) / 180.) * 1024.);
                position[0] = pos & 0xFF;
                position[1] = (pos >> 8) & 0xFF;
                this->sendResponse(0, position, 2);
                return;
            }
        }
    }

    this->sendResponse(AX12ErrorFlags::INSTRUCTION_ERROR, NULL, 0);
}

void Ax12ToPwmServo::sendResponse(unsigned char error, unsigned char * params, int params_length) {
    // Instruction packet : FF FF <ID> <LEN> <ERROR> <PAR0>..<PARN> <CKSUM>
    unsigned char buffer[params_length + 6];
    int i;
    int pos = 0;
    unsigned short checksum = 0;
    buffer[pos++] = 0xFF;
    buffer[pos++] = 0xFF;
    checksum += (buffer[pos++] = this->ax12Id);
    checksum += (buffer[pos++] = params_length + 2);
    checksum += (buffer[pos++] = error);

    for(i=0; i<params_length; i++){
        buffer[pos++] = params[i];
        checksum += params[i];
    }
    checksum = (~checksum) & 0xFF;
    buffer[pos++] = (unsigned char)checksum;

    this->responseSerial->write(buffer, pos);
    this->responseSerial->flush();
}

int Ax12ToPwmServo::currentPosition() {
    return this->servo.read();
}