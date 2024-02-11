#include <Arduino.h>

#define RPLIDARA2_UART_SPEED 115200

#define RPLIDARA2_FLAG_START_1 0xA5
#define RPLIDARA2_FLAG_START_2 0x5A

#define RPLIDARA2_COMMAND_STOP 0x25
#define RPLIDARA2_COMMAND_RESET 0x40
#define RPLIDARA2_COMMAND_SCAN 0x20
#define RPLIDARA2_COMMAND_EXPRESS_SCAN 0x82
#define RPLIDARA2_COMMAND_FORCE_SCAN 0x21
#define RPLIDARA2_COMMAND_GET_INFO 0x50
#define RPLIDARA2_COMMAND_GET_HEALTH 0x52
#define RPLIDARA2_COMMAND_GET_SAMPLERATE 0x59

#define RPLIDARA2_CIRCULAR_BUFFER_SIZE 32768

#define RPLIDARA2_UNIT_PER_MM 4
#define RPLIDARA2_UNIT_PER_DEGREE 64
#define RPLIDARA2_UNIT_PER_DEGREE_FLOAT 64.0
#define RPLIDARA2_TOTAL_ANGLE_UNITS RPLIDARA2_UNIT_PER_DEGREE * 360
// distance max : 8000mm * 4unit/mm
#define RPLIDARA2_MAX_DISTANCE_UNIT_EXCLUDED 8000 * RPLIDARA2_UNIT_PER_MM

#define RPLIDARA2_MAX_QUALITY 64

class RpLidarA2 {

    public:
        /**
         * @brief Construit une instance de pilotage du lidar
         * 
         * @param serial le port série à utiliser pour communiquer avec le lidar
         * @param rotationSwitch un pointeur vers un booléen qui gère la mise en rotation du lidar
         */
        RpLidarA2(HardwareSerial *serial, bool *rotationSwitch);

        /**
         * @brief Récupère des informations sur le lidar
         * 
         */
        void printInfos();

        bool fetchHealth(int *status, int *errorCode);

        /**
         * @brief Indique si le lidar est en train de scaner son environnement
         * 
         * @return true 
         * @return false 
         */
        bool isScanning();

        /**
         * @brief Démarre la rotation du lidar
         * 
         */
        void startRotation();

        /**
         * @brief Stope la rotation du lidar
         * 
         */
        void stopRotation();

        /**
         * @brief Indique si le lidar est en rotation
         * 
         * @return true 
         * @return false 
         */
        bool isRotating();

        /**
         * @brief Dépile les données envoyée par le lidar sur la liaison série
         * à appeler périodiquement et à fréquence élevée pour éviter l'engorgement de la liaison ce qui dégrade l'acquisition
         * 
         * @param bucket nombre de points renvoyés par le lidar à lire par tick
         * @return true 
         * @return false 
         */
        bool scanTick(int bucket = 10);

        /**
         * @brief Met en route la rotation du lidar et lui envoie la commande de début de scan
         * 
         * @return true 
         * @return false 
         */
        bool startScan();

        /**
         * @brief Dit au lidar d'arrêter le scan et arrête sa rotation
         * 
         */
        void stopScan();

        /**
         * @brief Envoie une commande de reset au lidar et arrête sa rotation éventuelle
         * 
         */
        void reset();

        /**
         * @brief Règle la qualité minimum d'un échantillon recevable 
         * 
         * @param min 
         */
        void setMinQuality(uint8_t min);

        /**
         * @brief Récupère la qualité minimale pour accepter un échantillon
         * 
         * @return uint8_t 
         */
        uint8_t getMinQuality();

        /**
         * @brief Règle la distance max des points à renvoyer, en mm
         * 
         * @param distance 
         */
        void setMaxDistance(uint16_t distance);

        /**
         * @brief Récupère la distance max des points en mm
         * 
         * @return uint16_t 
         */
        uint16_t getMaxDistance();

        /**
         * @brief Récupère le prochain point reçu
         * 
         * @param rawangle angle en unité brute qu'il faut convertir
         * @param distance distance en mm
         * @return true un point a été trouvé
         * @return false aucun point n'a été renvoyé
         */
        bool nextPoint(uint16_t *rawangle, uint16_t *distance);

    private:
        bool simpleScanRunning;
        bool *rotationSwitch;
        uint8_t minQualityThreshold;
        uint16_t maxRawDistance;

        uint16_t raw_distance_buffer[RPLIDARA2_TOTAL_ANGLE_UNITS]; // distance en unité brute
        uint16_t bufferReadPosition;

        HardwareSerial *serial;
        void flushSerialIn();
        void sendCmd(char command, char payloadSize, char *payloadData);
        bool readSingleResponse(char *response, char responseSize);
};