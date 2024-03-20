<meta name="description" content="Programming multi-agent in Java : use of an updated version of the Jade 
platform. Materials for Jade Tutorial : communication, protocols, votes, services, behaviors, ..." />

# Programming agents in Jade

[(web version)](https://emmanueladam.github.io/jade/)
[(french version)](https://github.com/EmmanuelADAM/jade/tree/master/)

Case study for ISSIA'23.

----
## Circular economy
@startuml
testdot
@enduml

<div hidden>
```
@startuml SADeMMaRUseCaseCoop
'!pragma layout smetana
left to right direction

actor admin  #yellow
actor user as us
actor "Repair\n Café" as rc #tan 
actor "Mag. pièces\n détâchées" as pd #green 
actor "Distributeur" as di #cyan 

'====================================================
package SademaarSite #EEFFEE{ 

'--------------------------------------------------
useCase enregistrerUser as "enregistrer\n personne
--
nom, prenom, email
adresse
compétences
"
useCase enregistrerCafe as "enregistrer\n repair café
--
adresse
familles de produits
horaires d'ouvertures"
'--------------------------------------------------


'--------------------------------------------------

useCase enregistrerPD as "enregistrer\n mag. pièces détachées
--
adresse
familles de produits
etats : neuf et/ou occasion
"   
'--------------------------------------------------


'--------------------------------------------------
useCase enregistrerDistri as "enregistrer\n distributeur
--
adresse
familles de produits
etats : neuf et/ou occasion
"   
'--------------------------------------------------

'--------------------------------------------------
useCase analyserPanne as "analyser panne
--
- **requiert**:documents\n(mode d'emploi, doc technique, ...)
" 
'--------------------------------------------------

'--------------------------------------------------
useCase reparerPanne as "réparer panne
--
- **requiert**:pièces disponibles 
" 
'--------------------------------------------------

'--------------------------------------------------
useCase commanderPart as "commander pièce
--
- **requiert**:specif pièce 
" 
'--------------------------------------------------

'--------------------------------------------------
useCase commanderProduit as "commander produit
--
- **requiert**:detail produit, etat 
" 
'--------------------------------------------------

'--------------------------------------------------
useCase "enregistrer" as enregistrer
'--------------------------------------------------

}
'====================================================

'====================================================
enregistrer <--admin 
enregistrerCafe <|-- enregistrer
rc --> enregistrerCafe
enregistrerPD <|-- enregistrer 
enregistrerPD <-- pd
enregistrerDistri <|-- enregistrer
enregistrerDistri  <-- di  
enregistrerUser <|-- enregistrer
enregistrerUser  <-- us
rc-->analyserPanne
us-->analyserPanne
reparerPanne .> analyserPanne: necessite
commanderPart .> analyserPanne: necessite
commanderProduit .> analyserPanne: necessite
reparerPanne .> commanderPart: necessite
rc<-- reparerPanne
us-->reparerPanne
pd-->commanderPart
us-->commanderPart
di-->commanderProduit
us-->commanderProduit
'====================================================

@enduml```
</div>



=======

