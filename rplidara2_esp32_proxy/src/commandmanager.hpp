#ifndef COMMANDMANAGER_H_
#define COMMANDMANAGER_H_

#include <Stream.h>
#include "rplidara2.h"
#include "scanmanager.hpp"

// Les commandes disponibles sur la liaison série
enum CommandManagerCommand {
    SERIAL_COMMAND_SCAN_START = 's',
    SERIAL_COMMAND_SCAN_STOP = 'h',
    SERIAL_COMMAND_SCAN_MODE = 'm',
    SERIAL_COMMAND_MOTOR_ROTATION = 'r',
    SERIAL_COMMAND_INFORMATION = 'i',
    SERIAL_COMMAND_RESET = 'e',
    SERIAL_COMMAND_POINT_MIN_QUALITY = 'q',
    SERIAL_COMMAND_POINT_MAX_DISTANCE = 'd',
    SERIAL_COMMAND_HEALTH = 'l',
    SERIAL_COMMAND_OUTPUT_FLAVOR = 'f',
};

// Les commandes disponibles sur la liaison série
enum CommandManagerOutputFlavor {
    CARTESIAN = 'c', // Coordonnées cartésiennes
    POLAR_RADIAN = 'r', // Coordonnées polaires en radians
    POLAR_DEGREES = 'd', // Coordonnées polaires en degrés
};

class CommandManager
{
    public:
        CommandManager(Stream* serial, RpLidarA2* lidar, ScanManager* scanManager);

        /**
         * @brief Méthode à appeler régulièrement pour gérer les commandes reçues sur al liaison série
         */
        void heartBeat();

    private:
        Stream *serial;
        RpLidarA2* lidar;
        ScanManager* scanManager;

        /**
         * @brief Analyse et traite les commandes qui arrivent sur le port série
         */
        void handleSerialCommand();

        /**
         * @brief Gère le démarrage/l'arrêt de la rotation du lidar
         */
        void handleCmdRotation();

        /**
         * @brief Gère la commande moteur PWM du lidar
         */
        void handleMotorControl();

        /**
         * @brief Gère la lecture/l'écriture de la distance max
         */
        void handleCmdMaxDistance();

        /**
         * @brief Gère la lecture/l'écriture de la qualité minimum attendu pour chaque point du lidar
         */
        void handleCmdMinQuality();

        /**
         * @brief Gère le paramétrage du mode de scan :
         * c = SCAN_MODE_CLUSTERING
         * o = SCAN_MODE_CLUSTERING_ONE_LINE
         * f = SCAN_MODE_FILTERED
         */
        void handleCmdScanMode();

        /**
         * @brief Gère le paramétrage de la sortie du scan :
         *  c = coordonnées cartésiennes
         *  d = coordonnées polaires en degrés
         */
        void handleCmdOutPutFormatFlavor();
};

#endif