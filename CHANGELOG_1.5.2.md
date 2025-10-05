# Cubic Racers v1.5.2 - Changelog

**Date:** 2025-10-05  
**Type:** Bug Fix & Network Enhancement  
**Contributors:** Community Contribution (LeChatOTapas)

---

## 🎯 Objectif de cette version

Cette version corrige un **bug critique** qui causait des crashes du serveur dédié et implémente un **système de synchronisation réseau robuste** pour le multijoueur.

---

## 🐛 Corrections de bugs critiques

### **Crash du serveur dédié résolu**
- **Problème:** Le serveur crashait avec l'erreur `IllegalArgumentException: Invalid message` lors de l'utilisation d'items de kart
- **Cause:** Système d'enregistrement réseau défectueux utilisant des "proxies" incompatibles avec Forge
- **Solution:** Refactorisation complète du système réseau avec `messageBuilder()` selon la documentation Forge 1.20.1

### **Chargement de classes client côté serveur**
- **Problème:** Le serveur tentait de charger des classes client (Minecraft.getInstance(), etc.)
- **Solution:** Séparation stricte du code client/serveur avec `@OnlyIn(Dist.CLIENT)` et `DistExecutor`

---

## ⚡ Nouvelles fonctionnalités

### **Système de synchronisation de position "Béton Armé" 💪**

Le plus gros problème du multijoueur était la désynchronisation des positions des karts entre joueurs. Cette version implémente un système de synchronisation professionnel :

#### **Nouveau message: `KartPositionSyncMessage`**
- Synchronise position (X, Y, Z)
- Synchronise rotation (yRot, xRot) 
- Synchronise vélocité pour prédiction côté client
- Synchronise vitesse affichée

#### **Caractéristiques techniques:**
- ✅ **Fréquence:** Envoi automatique toutes les 2 ticks (~100ms)
- ✅ **Autorité serveur:** Le serveur décide de la position réelle
- ✅ **Interpolation fluide:** Lerp de 5 ticks (250ms) pour mouvements naturels
- ✅ **Optimisé bande passante:** Utilise `TRACKING_ENTITY_AND_SELF` (n'envoie qu'aux joueurs proches)
- ✅ **Protection anti-triche:** Les clients ne peuvent plus mentir sur leur position
- ✅ **Pas de lag pour le joueur local:** Le contrôle direct reste instantané

#### **Résultat:**
Tous les joueurs voient maintenant les karts aux **bonnes positions** avec des mouvements **fluides et synchronisés**. Plus de problème de "je vois l'autre là-bas mais il est ailleurs en vrai" !

---

## 🔧 Améliorations techniques

### **Système réseau refactorisé**
- Suppression de l'approche "proxy" défectueuse
- Utilisation correcte de `messageBuilder()` avec des IDs uniques séquentiels
- Séparation claire des directions de messages (CLIENT_TO_SERVER vs SERVER_TO_CLIENT)
- Total de **16 messages** correctement enregistrés :
  - 10 messages Client → Server (contrôles, utilisation d'items)
  - 6 messages Server → Client (synchronisation, particules, effets)

### **Messages réseau:**
```
[CubicRacers] Network channels registered successfully! Total messages: 16
```

### **Fichiers modifiés:**
1. `Network.java` - Refactorisation complète du système réseau
2. `KartPositionSyncMessage.java` - **NOUVEAU:** Message de synchronisation de position
3. `TestKart.java` - Ajout de l'envoi automatique de position côté serveur
4. `gradle.properties` - Version mise à jour vers 1.5.2

---

## 📦 Installation

1. Remplacer le fichier JAR actuel par `cubicracers-1.5.2.jar`
2. Redémarrer le serveur
3. Tous les clients doivent également utiliser la version 1.5.2

---

## 🧪 Tests recommandés

- ✅ Serveur dédié démarre sans crash
- ✅ Utilisation d'items (Bob-Omb, Green Shell, etc.) fonctionne
- ✅ Plusieurs joueurs en kart voient les positions correctement
- ✅ Mouvements fluides sans saccades
- ✅ Aucun message "Invalid message" dans les logs

---

## 🔗 Compatibilité

- **Minecraft:** 1.20.1
- **Forge:** 47.3.0+
- **Compatible avec:** Version 1.5.1 (mise à jour recommandée)

---

## 📝 Notes pour le développeur

Cette contribution communautaire a été réalisée pour résoudre les problèmes de multijoueur rencontrés sur serveur dédié. Le code suit les meilleures pratiques Forge et la documentation officielle 1.20.1.

### Points d'attention:
- Le système de synchronisation utilise la bande passante réseau de manière optimale
- La fréquence de 2 ticks peut être ajustée dans `TestKart.java` (ligne ~116)
- L'interpolation de 5 ticks peut être ajustée dans `KartPositionSyncMessage.java` (ligne ~82)

### Architecture réseau:
```
Client → Server: Inputs, Actions des items
Server → Clients: Positions, Effets, Synchronisation
```

---

## 🙏 Remerciements

- **Projet original:** Quentin3010 (https://gitlab.com/Quentin3010/cubic-racers)
- **Contribution:** LeChatOTapas (correction bugs réseau + système de synchronisation)
- **Documentation:** Forge Documentation Team

---

## 📄 License

MIT License (maintenue depuis la version originale)
