# Personnel_PPE
Un annuaire d'entreprise écrit en java

## Contexte
La Maison des Ligues de Lorraine (M2L) est une organisation chargée de gérer les différentes ligues sportives de la région.

C'est un contexte de travail fictif utilisé par les étudiants en BTS SIO qui réalisent différents projets pour cette 
organisation.

## Présentation
Ce projet consiste à transformer une application en ligne de commande mono-utilisateur en application d'entreprise multi-utilisateur mise à la disposition
de tout les employés de l'organisation.

## Cahier des charges
Niveaux d'habilitations des utilisateurs sont les suivants: 
* Un simple employé de ligue peut ouvrir l’application et s’en servir comme un annuaire, mais il ne dispose d’aucun droit d’écriture.
* Un employé par ligue est admininstrateur et dispose de droits d’écriture peut gérer la liste des emloyés de sa propre ligue avec une application bureau.
* Le super-admininstrateur a accès en écriture à tous les employés des ligues. Il peut aussi gérer les comptes des administrateurs des ligues avec une application accessible en ligne de commande.


* L’application doit être rendue multi-utilisateurs grace à l’utilisation d’une base de données.

Les trois niveaux d’habilitation ci-dessus doivent être mis en place.
