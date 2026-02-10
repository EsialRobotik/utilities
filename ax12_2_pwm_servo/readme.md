Programme d'une carte d'adaptation AX12 vers Servo PWM

Il permet de lire des instructions normalement destinées à des servomoteurs de la gamme AX12 de la marque Dynamixel et de les transmettre à des servomoteur pilotés en PWM

https://emanual.robotis.com/docs/en/dxl/ax/ax-12a/

Fonctionnaltiés gérées :
- Réponse aux commandes PING sur les différentes adresses AX12 paramétrée pour simuler leur existence
- Réponse à la commande de lecture de position angulaire courante
- Recopie d'un angle AX12 compris entre 0 et 300° en un signal PWM compris entre 0 et 100%
- Vitesse de la liaison réglée à 115200 baud/s

Tableau de correspondance :

| Adresse AX12 | GPIO ESP32 C3 Zero |
| -- | -- |
| 20 | 0 |
| 21 | 1 |
| 22 | 2 |
| 23 | 3 |