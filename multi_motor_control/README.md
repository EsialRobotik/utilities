# Carte de contrôle multiples de moteurs

Code de la carte de contrôle moteur "Shadok" EsialRobotik.
Baptisée ainsi d'après sa première application qui a été de contrôler 3 pompes à air.

> La pompomanie, est l'acte de pomper excessivement. Cela est pratiqué naturellement par tous les Shadoks et c'est une de leur caractéristiques distinctives. [...]

([Source de la citation](https://shadoks.fandom.com/fr/wiki/Pompomanie))

Dans cette version, la carte peut contrôler 3 moteurs.

## Utilisation

La valeur de vitesse des moteurs doit être comprises entre 255 (marche avant) et -255 (marche arrière).<br>Une valeur de 0 permet de stopper un moteur, mais n'active pas de frein moteur.<br>Toute valeur en dehors des bornes est ramenée à la valeur de la borne la plus proche.

La numérotation des moteurs commence à 0.

Pour interragir avec la carte de contrôle, envoyer sur la liaison série USB de l'Arduino en une fois `<commande><argument>\n` :

## Commandes disponibles

| Commande | Argument(s) | Description |
|--|--|--|
| s | moteur, vitesse (optionnel) | Règle la vitesse d'un moteur |
| g | vitesse (optionnel) | Si argument présent : règle la vitesse de tous les moteurs. Si absent, renvoie sur une ligne les vitesses de tous les moteurs. Exemple : `0:255;1:0;2-255` |
| h | *aucun* | Arrêt d'urgence : désactive tous les moteurs |
| ? | *aucun* | Affiche la liste des commandes disponibles |

## Exemples

| Commande | Signification |
|--|--|
| s0128 | Régle la vitesse du moteur 0 à la moité de la puissance en marche avant |
| s1 | Lit la vitesse courante du moteur 1 |
| g-255 | Régle tous les moteurs à pleine puissance en marche arrière |
| h | Arrête tous le smoteurs (puissance à 0)
| ? | Affiche l'aide
