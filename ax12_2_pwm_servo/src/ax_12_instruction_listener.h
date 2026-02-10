#ifndef _AX_12_INSTRUCTION_LISTENER
#define _AX_12_INSTRUCTION_LISTENER

#include <arduino.h>
#include "ax_12_enums.h"

/// @brief Permet d'écouter une lisiaon série et d'en détecter/extraire les instructions destinées à des AX12
class Ax12InstructionListener {

  public:
    Ax12InstructionListener(HardwareSerial *hwSerial);

    /// @brief Lance une détection et tente un éventuel décoage d'une instruction reçue sur la liaison série
    /// @param instr 
    /// @return true si une instruction valide a été reçue
    bool heartBeat();

    /// Renvoie le résultat de la dernière tentative de lecture d'instruction
    Ax12InstructionData getLastInstruction();

  private:
    // Liaison série vers l'AX12
    HardwareSerial* hwSerial;

    /// @brief Résultat de la dernière tentative de lecture d'instruction
    Ax12InstructionData lastInstruction;

    /// @brief Purge toutes les données en entrée de la liaison série
    inline void flushSerialInput();
};

#endif // _AX_12_INSTRUCTION_LISTENER
