# Proxy Lidar RpLidarA2
Proxy offrant une couche d'abstraction pour le pilotage et la récupération de data en provenance d'un RpLidar A2.
Contient un mode de clustering simple pour ne renvoyer que les points intéressants sur la liaison série.

## Utilisation
Envoyer sur la liaison série en une fois "\<commande>\<argument>\n" :
| Commande | Argument | Description |
|--|--|--|
| s | *aucun* | Démarre le scan |
| h | *aucun* | Arrête le scan |
| m | mode de scan | Renvoie ou règle le mode de scan. Argument vide pour avoir la valeur courante ou alors envoyer une valeur parmi celles listées dans la colonne 'Mode' du tableau 'Modes de scan' |
| r | activer | Active '1' ou désactive '0' la rotation du moteur |
| i | *aucun* | Récupère les informations sur le lidar |
| e | *aucun* | Reset : stope la rotation et reset le lidar |
| q | qualité | Règle la qualité minimum (1 à 63) en dessous de laquelle tout point sera rejeté des traitements. Argument vide pour avoir la valeur courante |
| d | distance mm | Règle la distance en mm au dessus de laquelle ignorer les points reçu du lidar. Argument vide pour avoir la valeur courante |
| l | *aucun* | Renvoie la santé du lidar sous la forme : "\<status>/\<code>\n"  |
| f | format | Renvoie ou règle le format des coordonnées renvoyées. Argument vide pour avoir la valeur courante ou alors envoyer une valeur parmi celles listées dans la colonne 'Format' du tableau 'Formats de retour' |

## Modes de scan
Chaque mode intègre le pré-filtrage des points q et d. Respectivement qualité en dessous de laquelle un point est ignoré et distance au delà de laquelle un point est ignoré.
| Mode | Code argument | Description |
|--|--|--|
| filtrage | f | Les points filtrés sont renvoyés directement sur la liaison série au fil de l'eau, ligne par ligne (1 ligne = 1 point). |
| clustering | c | Effectue un clustering sur le nuage de points filtrés et renvoie chaque coordonnées de cluster trouvé sur une ligne différente. | 342.80;1802.00 |
| clustering une ligne | o | Effectue un clustering sur le nuage de points filtrés et renvoie toutes les coordonnées des clusters trouvés sur une suele ligne. Deux coordonnées sont séprées par un '#' |

## Formats de retours
Le Lidar est considéré comme étant à l'origine du repère.
Le format des points renvoyés sur la liaison série peut être réglé parmi ceux ci-après :
| Format | Code argument | Description | Exemple |
|--|--|--|--|
| cartésien | c | Coordonnées cartésiennes en mm.| -7231.14;-1829.55 |
| polaire degrés| d | Cordonnées polaires dont l'angle est exprimé en degrés et la distance en mm. | 342.80;1802.00 |
| polaire radians | r | Coordonnées polaires dont l'angle est exprimé en degrés et la distance en mm. | 2.83;1663.00 |