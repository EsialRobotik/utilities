#ifndef ACFDIMPLEMENTATION_H_
#define ACFDIMPLEMENTATION_H_

#include "points.hpp"

// Si on veut augmenter la résolution il faut faire des choses obscures pour étendre la RAM de l'ESP32
#define POINTS_PAR_DEGRES 32

#define POINTS_TOTAUX POINTS_PAR_DEGRES * 360

class ACFDImplementation {

    public:
        /**
         * @brief Construct a new ACFDImplementation object
         * 
         * @param ignoreDistanceAbove 
         * @param minPointsPerCluster 
         * @param maxPointsPerCluster 
         * @param maxClusterCount 
         * @param maxDistanceBetween2Pointsmm 
         * @param maxAngleBetween2PointsDegrees 
         * @param maxPointsToSkip 
         */
        ACFDImplementation(
	        float ignoreDistanceAbove,
	        int minPointsPerCluster,
	        int maxPointsPerCluster,
	        int maxClusterCount,
	        float maxDistanceBetween2Pointsmm,
	        float maxAngleBetween2PointsDegrees,
	        int maxPointsToSkip
        );

        /**
         * @brief Ajoute un point dans la liste des points à analyser.
         * Les points sont remis à zéro après un clustering.
         * Si deux appels sont réalisés sur le même angle, la valeur du dernier appel écrase la première.
         *  
         * @param angle en degrés
         * @param distance en millimètres
         */
        void addPoint(float angle, float distance);

        /**
         * @brief Effectue l'analyse de la liste des points et reset les points ajoutés au fur et à mesure de leur lecture.
         * Reste également le curseur de lecture des clusters renovyés par @see nextClusterCoordinates()
         * 
         * @return ClustersBag* Pointeur vers la liste des clusters trouvés
         */
        void doClustering();

        /**
         * @brief indique si les coordonnées d'un cluster sont prêtes à être récupérées par @see nextClusterCoordinates()
         * Appeler N fois cette fonction sans appeler doClustering() ou nextClusterCoordinates() renverra toujours le même résultat
         * 
         * @return bool
         */
        bool hasNextClusterCoordinates();

        /**
         * @brief récupère les coordonnées du prochain cluster toruvé. à n'appeler que si @see hasNextClusterCoordinates() a renvoyé true.
         * 
         * @return Point 
         */
        Point nextClusterCoordinates();

    private:
        float points[POINTS_TOTAUX];

        /**
         * @brief Distance en millimètres au delà de laquelle on ignore un point lors de son ajout via @see addPoint
         * 
         */
        float ignoreDistanceAbove;

        /**
         * @brief Nombre minimum de points attendus dans un cluster
         * 
         */
        int minPointsPerCluster;

        /**
         * @brief Nombre maximum de points dans un cluster
         * 
         */
        int maxPointsPerCluster;

        /**
         * @brief Nombre maximum de cluster à chercher
         * 
         */
        int maxClusterCount;

        /**
         * @brief Distance max en millimètres entre 2 points pour qu'ils soient considérés comme appartenant au même cluster
         * 
         */
        float maxDistanceBetween2Pointsmm;

        /**
         * @brief Angle maximum en degrés entre 2 points pour qu'ils soient considérés comme appartenant au même cluster
         * 
         */
        float maxAngleBetween2PointsDegrees;

        /**
         * @brief Nombre maximum de points qu'on peut ignorer avant de considérer que le cluster est terminé
         * 
         */
        int maxPointsToSkip;

        /**
         * @brief Liste des coordonnées des clusters trouvés
         * 
         */
        Point* clustersCoordinates;

        /**
         * @brief Curseur d'écriture des coordonnées de cluster utilisé par @see doClustering()
         * Sert de borne de fin au cusrseur de lecture @see clusterCoordinatesCursor
         * 
         */
        int clusterCoordinatesWriteCursor;

        /**
         * @brief Curseur de lecture des coordonnées de cluster utilisé par @see hasNextClusterCoordinates()
         * 
         */
        int clusterCoordinatesReadCursor;

        /**
         * @brief Liste des points du cluster de travail
         * 
         */
        PolarPoint* currentClusterPoints;

        /**
         * @brief Dernier point de la liste des points du cluster de travail
         * 
         */
        int currentClusterPointsIndex;
};

#endif