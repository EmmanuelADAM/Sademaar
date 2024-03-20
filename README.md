<meta name="description" content="Programming multi-agent in Java : use of an updated version of the Jade 
platform. Materials for Jade Tutorial : communication, protocols, votes, services, behaviors, ..." />

# SADéMMaR :
## Sensibilisation- Accompagnement et aide à la Décision des Ménages dans le processus de Maintenance et Réparation

----
Ce dépôt contient des codes pour des agents simulant des interactions dans le cadre de la réparation de produit.


Voici le scenario:
 - des utilisateurs possèdent des produits
 - des produits peuvent être en panne
 - plusieurs solutions sont disponibles :
   - tuto sur des website
   - repair cafés
   - distributeur de pièces de rechange
   - distributeurs.

Vous trouverez dans ce fichier : 
- [le use case](#usecase)
- [la specification](#specification)
- [la specification détaillée](#specificationdetaillee)
- [la démonstration et le détail d'implémentation](#implementation)

---
### Use case<a name="usecase"></a>
Voici le use case prévu pour la conception d'une application web de gestion des échanges.

<img src="SademarUseCaseCoop.svg" alt="Sademaar UseCase Coop" width="600"/>


---
### Specification<a name="specification"></a>

- Lorsque le produit ne fonctionne plus ; l'utilisateur essaie de trouver une aide : 
  - par lui-même, 
  - localement (repair coffee), 
  - en recherchant une pièce dans la liste des distributeurs de pièces, 
  - en remplaçant le produit.

- Le choix d'un repair café dépend de critères tels que le délai d'attente, la confiance, la position,...
- Le choix d'une pièce dépend également de critères tels que le coût, ...
- Le choix d'un distributeur dépend de la confiance et du prix

Très généralement, voici un diagrame de séquence pour la recherche de repair café : 
![declarationPanne.png](declarationPanne.png)

Voici les détails des interactions entre les différents acteurs : 
![coopReparation.png](coopReparation.png)

---
### Specification détaillée<a name="specificationdetaillee"></a>

Voici sous forme de diagramme d'activités l'ensemble du procédé inlut dans les codes fournis : 
![actititesPanne.png](actititesPanne.png)

---
### Implementation<a name="implementation"></a>

**Testez en lançant l'application [Sademaar.jar](Sademaar.jar)**.

Dans le code, 
- la classe principale est `LaunchAgents`:
  - des produits types sont créés, ainsi que des pièces types.
- les agents `Distributor` reçoivent des produits aléatoirement et fixent à chacun leur prix
- les agents `SparePartsStore` reçoivent des pièces aléatoirement et fixent à chacune leur prix
- les agents `RepairCoffee` se voient attribuer des spécialités (des types de produits)
- les agents `UserAgents` reçoivent des produits

- Dans la fenêtre d'un user agent, on sélectionne un produit et on transmet une demande à un repair café.
- une panne est détectée sur une pièce, elle peut être légère ou lourde, voire dangereuse. 

#### Les produits
Dans cette version, des produits sont générés à chaque instance. 
Il est possible également d'utiliser un fichier de données contenant des définitions de produit.

Un produit contient : 
- un identifiant,
- un nom,
- un prix de base,
- un type de produit,
- une liste de pièces réparables/inter-changeables :
  - des pièces sont légères, d'un niveau de difficulté de réparation faible
  - des pièces sont complexes, lourdes, voire dangereuses, d'un niveau de difficulté de réparation élevé, voire impossible
- la pièce éventuellement déclarée en panne.

Chaque pièce possède :
- un identifiant,
- un nom,
- un prix de base,
- un niveau de difficulté de réparation,
- le fait qu'elle soit sensible et très déconseillée pour la réparation par une personne non assermentée.

C'est une définition très générique qui peut être adapté si scénario spécifique.

L'ajout de la notion de possibilité d'avoir une pièce ou un produit de seconde main peut être ajouté assez rapidement. 

#### Les réparations
Une trace des réparations par agent utilisateur est réalisé.

Une réparation contient :
- l'émetteur de la demande de réparation,
- son niveau estimé d'expertise,
- son temps d'attente maximum,
- le produit sur lequel se porte la réparation,
- les pièces utilisées,
- les agents impliqués,
- la date de début,
- la date de fin,
- le cout de la réparation,
- le succès ou l'échec de la réparation,
- la liste des étapes de réparation.

Une étape de réparation est de type (entre autres) : 
- Nouvelle, Demande de RdzVs au Repair Café,  RdzVs, Diagnostique, Réparation, Recherche de pièce, Réception de pièce, Recherche de produit de remplacement, Produit reçu, Succès de réparation, Echec de réparation.
- et contient la date et les agents impliqués. 

#### Les critères
Les critères sont simples dans la version présentée. Ils peuvent être approfondis, adaptés dans les zones indiquées dans les codes. 

Choix d'un repair café : 
- dépend de la date proposée, de la confiance, de la position géographique
Choix d'une offre d'un distributeur de pièces détachées :
- dépend du prix, de la confiance,
Choix d'une offre d'un distributeur de produits :
- dépend du prix, de la confiance.


---
