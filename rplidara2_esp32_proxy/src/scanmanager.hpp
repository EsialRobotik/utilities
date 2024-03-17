#ifndef SCANMANAGER_H_
#define SCANMANAGER_H_

#include <Stream.h>
#include "rplidara2.h"
#include "ACFDImplementation.hpp"

/**
 * @brief Modes de scan disponibles 
 */
enum SCAN_MANAGER_SCAN_MODE {
  SCAN_MODE_FILTERED, // Filtré = les données du lidar qui respectent les filtres de distance et de qualité sont renvoyées
  SCAN_MODE_CLUSTERING, // Clustering = les données du lidar sont analysées pour en extraire les groupes de points trouvés et en renvoyer leur barycentre
};

enum SCAN_MANAGER_OUTPUT_FORMAT {
  OUTPUT_FORMAT_CARTESIAN, // Coordonnées cartésiennes
  OUTPUT_FORMAT_POLAR_RADIAN, // Coordonnées polaires en radians
  OUTPUT_FORMAT_POLAR_DEGREES, // Coordonnées polaires en degrés
};

class ScanManager {
    public:
        ScanManager(Stream* serial, RpLidarA2* lidar, ACFDImplementation* acfd);

        SCAN_MANAGER_SCAN_MODE getMode();

        void setMode(SCAN_MANAGER_SCAN_MODE mode);

        SCAN_MANAGER_OUTPUT_FORMAT getOutputFormat();

        void setOutputFormat(SCAN_MANAGER_OUTPUT_FORMAT format);

        /**
         * @brief Méthode à appeler très fréquement pour gérer la transmission ud scan sur la liaison série
         */
        void heartBeat();
    private:
        Stream* serial;
        RpLidarA2* lidar;
        ACFDImplementation* acfd;
        SCAN_MANAGER_SCAN_MODE scanMode;
        SCAN_MANAGER_OUTPUT_FORMAT outputFormat = OUTPUT_FORMAT_POLAR_RADIAN; // par défaut les coordonnées seront au format polaire avec des angles en radians
        unsigned long clusteringLastExecution = 0; // timestamp du dernier clustering effectué
        unsigned long clusteringFrequency = 200; // période entre 2 exécutions du clustering en ms

        inline void printPoint(Point p) {
          PolarPoint pp;
          switch (outputFormat) {
              case OUTPUT_FORMAT_CARTESIAN:
                  serial->print(p.x);
                  serial->print(';');
                  serial->println(p.y);
              break;
              case OUTPUT_FORMAT_POLAR_RADIAN:
                  pp = cartesianToPolar(p, true);
                  serial->print(pp.angle);
                  serial->print(';');
                  serial->println(pp.distance);
              break;
              case OUTPUT_FORMAT_POLAR_DEGREES:
                  pp = cartesianToPolar(p, false);
                  serial->print(pp.angle);
                  serial->print(';');
                  serial->println(pp.distance);
              break;
          }
        }
};

#endif