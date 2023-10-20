<meta name="description" content="Programming multi-agent in Java : use of an updated version of the Jade 
platform. Materials for Jade Tutorial : communication, protocols, votes, services, behaviors, ..." />

# Programming agents in Jade

[(web version)](https://emmanueladam.github.io/jade/)
[(french version)](https://github.com/EmmanuelADAM/jade/tree/master/)

Case study for ISSIA'23.

----
## Circular economy

@startuml actititesPanne
|Utilisateur|
start
partition "déclarer panne"{
:se connecter;
split
:définir produit;
split again
:définir problème;
end split
:évaluer sa compétence sur le problème;
:demander information sur aides financières;
:demander liste de repairs cafés ;
:choisir selon 
    -disponibilité, proximité, avis personnel ;
}
note: cas sans prise\n de rdz-vs
|#AntiqueWhite|Repair Café|
:analyser la panne
- avec l'utilisateur;
:foo3;
|Swimlane1|
:foo4;
|Swimlane2|
:foo5;
stop
@enduml


<!--
```
@startuml declarationPanne

participant user1 
participant "Site\nSademaar" as site #coral 
participant "Repair\n Café 1" as rc1 #tan 
participant "Repair\n Café 2" as rc2 #tan 
participant "Repair\n Café 3" as rc3 #tan 

==DECLARE PANNE==
user1 -> site: description \ndu produit en panne
site <- site:choix\nrepair cafés adaptés
site -> rc1: msg1
' site -> rc2: msg1
site -> rc3: msg1
rc1 --> user1: rdz-vs (creneau)
' rc2 --> user1: refus (ne gère pas ce type de produit)
rc3 --> user1: rdz-vs (creneau)
user1 <- user1: choix rdz-vs
user1 -> site: acceptation\n rdz-vs rc3
site -> rc3: acceptation rdz-vs
@enduml```
-->

<!--
```
@startuml reparationPanne
skinparam responseMessageBelowArrow true
participant user1 
participant "Site\nSademaar" as site #coral 
participant "Repair\n Café 1" as rc1 #tan 
participant "Repair\n Café 2" as rc2 #tan 
participant "Repair\n Café 3" as rc3 #tan 
participant "Pièces\n détâchées 1" as pd1 #green 
participant "Pièces\n détâchées 2" as pd2 #green 
participant "Pièces\n détâchées\n2nde main 1" as pds1 #lightgreen 
participant "Distributeur 1" as d1 #cyan 
participant "Distributeur 2" as d2 #cyan 
participant "Distributeur\n2nde main 1" as ds1 #lightcyan 
participant "Distributeur\n2nde main 2" as ds2 #lightcyan 

==ANALYSE :  PANNE REPARABLE PAR/AVEC LA PERSONNE==
user1 -> rc3: presentation produit
activate rc3
activate user1
rc3 -> rc3: analyse produit
rc3 -> user1: pièce P identifiée
deactivate user1
deactivate rc3
group#CCCCFF #EFEFFF dmd devis
user1 -> site: consulter aide financiere
site --> user1 : aides possibles
user1 -> pd1: dmd devis P
pd1 --> user1: devis P1
user1 -> pd2: dmd devis P
pd2 --> user1: pas en stock
user1 -> pds1: dmd devis P
pds1 --> user1: devis P'1
end
user1 <- user1: choix selon \nprix et confiance
activate user1
deactivate user1

alt#Gold #BBFFDD interet financier à réparer
    user1 -> pds1:refus
    user1 -> pd1:achat P1
    pd1 --> user1:confirme vente P1
    
    alt#Gold #EEFFEE competence suffisante
        user1 <- user1: reparation
        activate user1
        deactivate user1
    else #DDFFDD competence insuffisante
        alt#Gold #FEFFEE desir garder repair cafe initial
            user1 -> rc3:demande rdz-vs
            user1 <-- rc3:rdz-vs reparation
            user1 -> rc3:reparation assistee
            activate user1
            activate rc3
            deactivate user1
            deactivate rc3
            user1 <-- rc3
        else #ECF9EA recherche de repair café
            user1 -> site:demande liste repair café\n selon critères
            user1 <--- site: contacts
            user1 -> rc1:demande rdz-vs
            user1 <-- rc1
            user1 -> rc2:demande rdz-vs
            user1 <-- rc2
            user1 <- user1: choix repair cafe\n pour reparation
            user1 -> rc1:reparation assistee
        end
    end
else #FFEAEA interet limité dans la réparation: remplacement
    user1 -> site:dmd distributeurs type produit
    note left: coût de la réparation\n proche de l'achat d'un nouveau produit.\n Contact des distributeurs adéquats.
    user1 <-- site:liste distributeurs
    user1 -> d1:dmd produit remplacement
    user1 <-- d1:  nvo_produit
    user1 -> ds1:dmd produit remplacement
    user1 <-- d1:  produit_occasion
    user1 -> ds1:confirme achat produit_occasion
    user1 <-- ds1
end

@enduml```

-->

<!--
```
@startuml ReparationImpossible

participant user1 
participant "Site\nSademaar" as site #coral 
participant "Repair\n Café 1" as rc1 #tan 
participant "Repair\n Café 2" as rc2 #tan 
participant "Repair\n Café 3" as rc3 #tan 
participant "Pièces\n détâchées 1" as pd1 #green 
participant "Pièces\n détâchées 2" as pd2 #green 
participant "Pièces\n détâchées\n2nde main 1" as pds1 #lightgreen 
participant "Distributeur 1" as d1 #cyan 
participant "Distributeur 2" as d2 #cyan 
participant "Distributeur\n2nde main 1" as ds1 #lightcyan 

==ANALYSE :  PANNE NON REPARABLE PAR/AVEC LA PERSONNE==
    user1 -> site:dmd distributeurs type produit
    note left: panne diagnostiquée\n comme non réparable
    user1 <-- site:liste distributeurs
    user1 -> d1:dmd produit remplacement
    user1 <-- d1:  nvo_produit
    user1 -> ds1:dmd produit remplacement
    user1 <-- ds1:  produit_occasion
    user1 <- user1: choix
    user1 -> d1:refus
    user1 -> ds1:confirme achat produit_occasion
    user1 <-- ds1

@enduml```

-->

