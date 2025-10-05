# 🏎️ Cubic Racers v1.5.2 - Proposition de Contribution

**À l'attention de:** Quentin3010 (Propriétaire du projet)  
**De:** Contributeur communautaire (LeChatOTapas)  
**Date:** 5 Octobre 2025  
**Projet:** https://gitlab.com/Quentin3010/cubic-racers

---

## 👋 Introduction

Bonjour Quentin,

Je me permets de vous contacter concernant votre excellent mod **Cubic Racers**. Je ne suis pas affilié au projet, mais j'ai identifié et corrigé des bugs critiques qui empêchaient le fonctionnement sur serveur dédié.

Je vous propose cette contribution en version **1.5.2** avec l'espoir qu'elle puisse être intégrée dans la version officielle.

---

## 🚨 Problèmes résolus

### 1. **Crash du serveur dédié** (Critique)

**Erreur rencontrée:**
```
[Server thread/ERROR]: Received invalid message 
me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage 
on channel cubicracers:network

Caused by: java.lang.IllegalArgumentException: Invalid message
```

**Impact:** Le serveur crashait dès qu'un joueur utilisait un Bob-Omb ou d'autres items de kart.

**Cause identifiée:** Le système réseau utilisait une approche "proxy" non compatible avec Forge qui créait des messages invalides.

### 2. **Désynchronisation des positions en multijoueur**

**Problème:** Les joueurs ne voyaient pas les autres karts aux bonnes positions, rendant les courses multijoueur injouables pour le classement.

---

## ✅ Solutions implémentées

### **1. Refactorisation complète du système réseau**

J'ai réécrit le fichier `Network.java` en suivant strictement la documentation Forge 1.20.1 :

- ✅ Suppression des proxies défectueux
- ✅ Utilisation correcte de `messageBuilder()` avec IDs séquentiels
- ✅ Séparation claire client/serveur (`NetworkDirection`)
- ✅ 16 messages correctement enregistrés

**Résultat:** Plus aucun crash serveur, tous les messages fonctionnent.

### **2. Nouveau système de synchronisation de position**

J'ai créé un nouveau message `KartPositionSyncMessage` qui :

- Envoie automatiquement la position depuis le serveur toutes les 2 ticks (~100ms)
- Utilise l'interpolation fluide (`lerpTo`) pour des mouvements naturels
- Optimise la bande passante (n'envoie qu'aux joueurs proches)
- Donne l'autorité au serveur (anti-triche)

**Résultat:** Synchronisation parfaite des positions, courses multijoueur fluides.

---

## 📁 Fichiers modifiés

### **Fichiers principaux:**

1. **`Network.java`** - Système réseau complètement refactorisé
2. **`KartPositionSyncMessage.java`** - NOUVEAU fichier pour sync position
3. **`TestKart.java`** - Ajout de l'envoi automatique de position
4. **`gradle.properties`** - Version mise à jour vers 1.5.2

### **Fichiers de documentation:**

5. **`CHANGELOG_1.5.2.md`** - Détails complets des modifications
6. **`CONTRIBUTION_PROPOSAL.md`** - Ce document

---

## 🧪 Tests effectués

- ✅ Compilation réussie sans erreurs
- ✅ Serveur dédié démarre sans crash
- ✅ Tous les items fonctionnent (Bob-Omb, Green Shell, etc.)
- ✅ Positions synchronisées en multijoueur
- ✅ Aucune régression détectée

---

## 📦 Comment intégrer cette version

### **Option 1: Fusion directe**
Remplacer les fichiers modifiés dans votre projet avec ceux de la v1.5.2

### **Option 2: Merge Request GitLab** (Recommandé)
Je peux créer une Merge Request sur votre GitLab avec :
- Tous les changements commentés
- Tests de validation
- Documentation détaillée

### **Option 3: Review puis adaptation**
Vous pouvez réviser mes modifications et les adapter selon vos standards de code

---

## 💡 Pourquoi proposer cette contribution ?

1. **Passion pour le projet:** Votre mod est excellent et mérite de fonctionner parfaitement en multijoueur
2. **Respect du travail original:** J'ai conservé votre architecture et style de code
3. **Communauté:** Les joueurs rencontrent ces bugs, cette correction les aidera
4. **Open Source:** L'esprit du projet MIT est de s'entraider

---

## 📊 Comparaison des versions

| Fonctionnalité | v1.5.1 | v1.5.2 |
|---|---|---|
| Serveur dédié stable | ❌ Crash | ✅ Stable |
| Messages réseau | ❌ Proxies invalides | ✅ messageBuilder() |
| Sync position multijoueur | ⚠️ Désynchronisé | ✅ Synchronisé |
| Fréquence de sync | Jamais | 2 ticks (100ms) |
| Interpolation fluide | Non | ✅ Lerp 5 ticks |
| Protection anti-triche | Non | ✅ Autorité serveur |

---

## 🔗 Ressources

- **Build compilé:** `build/libs/cubicracers-1.5.2.jar`
- **Changelog détaillé:** `CHANGELOG_1.5.2.md`
- **Code source:** Tous les fichiers modifiés disponibles
- **Documentation Forge utilisée:** https://docs.minecraftforge.net/en/1.20.1/

---

## 🤝 Ma proposition

Je vous propose **gratuitement** cette contribution pour améliorer votre mod. Vous êtes libre de :

- ✅ L'intégrer telle quelle
- ✅ La modifier selon vos besoins
- ✅ Me créditer ou non dans les notes de version
- ✅ Me contacter pour des clarifications

**Mon seul souhait:** Que Cubic Racers fonctionne parfaitement en multijoueur pour tous les joueurs ! 🏁

---

## 📬 Contact

Si vous souhaitez discuter de cette contribution ou avez des questions :

- **GitLab:** Créer une issue sur votre projet
- **Merge Request:** Je peux en créer une si vous le souhaitez
- **Email:** (À compléter si vous souhaitez être contacté)

---

## 🙏 Remerciements

Merci pour votre travail exceptionnel sur Cubic Racers. C'est un plaisir de contribuer à un projet aussi bien conçu.

Cordialement,  
Un contributeur passionné

---

**P.S.:** Tous les fichiers sont prêts à être intégrés. Le mod est fonctionnel et testé. N'hésitez pas à me poser des questions techniques ! 🚀
