# 🚀 Guide de Soumission - Cubic Racers v1.5.2

**Contributeur:** LeChatOTapas  
**Date:** 5 Octobre 2025

---

## ⚠️ PROBLÈME: Ton projet n'est PAS un fork officiel

**Situation actuelle:**
- Tu as un projet GitLab séparé
- Il n'est pas lié au projet de Quentin
- Tu ne peux pas créer de Merge Request

**Solution:** Créer un VRAI fork via GitLab

---

## 🔧 SOLUTION: Créer un vrai fork GitLab

### **Méthode 1: Fork via GitLab (RECOMMANDÉ)**

1. **Supprime ton projet actuel sur GitLab:**
   - Va sur https://gitlab.com/LeChatOTapas/cubic-racers
   - Settings → General → Advanced
   - "Delete project"
   - Confirme la suppression

2. **Crée un VRAI fork:**
   - Va sur le projet original: https://gitlab.com/Quentin3010/cubic-racers
   - Clique sur le bouton **"Fork"** en haut à droite
   - Sélectionne ton namespace (LeChatOTapas)
   - Attends que le fork se crée

3. **Configure ton dépôt local:**
   ```bash
   # Supprime l'ancien remote
   git remote remove myfork
   
   # Ajoute le nouveau fork
   git remote add origin https://gitlab.com/LeChatOTapas/cubic-racers.git
   
   # Push ta branche vers ton fork
   git push origin feature/v1.5.2-network-fix
   ```

4. **Maintenant tu PEUX créer une Merge Request:**
   - Va sur ton fork: https://gitlab.com/LeChatOTapas/cubic-racers
   - Tu verras un bouton "Create merge request"
   - Source: ton fork → `feature/v1.5.2-network-fix`
   - Target: Quentin3010/cubic-racers → `main`

---

### **Méthode 2: Issue + Patch (ALTERNATIVE - Plus simple)**

Si tu ne veux pas gérer les forks, utilise simplement une Issue :

1. **Va directement sur le projet de Quentin:**
   https://gitlab.com/Quentin3010/cubic-racers/-/issues/new

2. **Crée une Issue avec le patch:**
   - Titre: `[CONTRIBUTION] v1.5.2 - Fix crash serveur dédié + sync multijoueur`
   - Description ci-dessous
   - Attache le fichier `v1.5.2-LeChatOTapas.patch`
   - Attache le JAR compilé

---

## ✅ Ce qui a été fait

Ta branche `feature/v1.5.2-network-fix` est prête localement avec tous les changements !

**📦 Fichiers disponibles:**
- Patch Git: `v1.5.2-LeChatOTapas.patch`
- JAR compilé: `build/libs/cubicracers-1.5.2.jar`
- Documentation complète

---

## 🎯 Comment soumettre au propriétaire (Quentin3010)

### **Option 1: Issue GitLab avec Patch** ⭐ (LE PLUS SIMPLE)

**Pas besoin de fork pour ça !**

1. **Va sur le projet original:**
   https://gitlab.com/Quentin3010/cubic-racers/-/issues/new

2. **Titre:**
   ```
   [CONTRIBUTION] v1.5.2 - Fix crash serveur dédié + sync multijoueur
   ```

3. **Description:**
   ```markdown
   Bonjour Quentin ! 👋

   J'ai corrigé les bugs critiques qui faisaient crasher ton mod sur serveur dédié 
   et ajouté un système de synchronisation multijoueur performant.

   ## 🐛 Problèmes résolus
   - ❌ Crash serveur avec "Invalid message" lors de l'utilisation d'items
   - ❌ Désynchronisation des positions en multijoueur

   ## ✅ Solutions implémentées
   - ✅ Système réseau refactorisé (16 messages correctement enregistrés)
   - ✅ Nouveau système de sync position (toutes les 100ms avec interpolation fluide)
   - ✅ Autorité serveur pour anti-triche
   - ✅ Plus aucun crash

   ## 📦 Fichiers joints
   - **Patch Git:** `v1.5.2-LeChatOTapas.patch` (à appliquer avec `git apply`)
   - **JAR compilé:** `cubicracers-1.5.2.jar` (pour tester directement)
   
   ## 📊 Comparaison
   | Fonctionnalité | v1.5.1 | v1.5.2 |
   |---|---|---|
   | Serveur dédié | ❌ Crash | ✅ Stable |
   | Messages réseau | ❌ Proxies invalides | ✅ messageBuilder() |
   | Sync multijoueur | ⚠️ Désynchronisé | ✅ 100ms + lerp |

   ## 🧪 Tests effectués
   - ✅ Compilation réussie
   - ✅ Serveur dédié stable
   - ✅ Tous les items fonctionnent
   - ✅ Positions synchronisées
   - ✅ Aucune régression

   ## 📖 Documentation
   J'ai préparé une documentation complète :
   - CHANGELOG complet avec détails techniques
   - Guide d'installation
   - Détails d'architecture réseau

   ## 🔧 Comment appliquer
   ```bash
   # Méthode 1: Avec le patch
   git apply v1.5.2-LeChatOTapas.patch
   
   # Méthode 2: Tester le JAR directement
   # Remplace le JAR dans ton serveur
   ```

   ## 🤝 Proposition
   Tu peux :
   - Appliquer le patch directement
   - Intégrer les modifications à ta façon
   - Me demander des clarifications
   - Refuser si ça ne convient pas

   **C'est une contribution gratuite, fais-en ce que tu veux !** 🏁

   Cordialement,  
   LeChatOTapas
   ```

4. **Attache les fichiers:**
   - Clique sur l'icône trombone
   - Ajoute `v1.5.2-LeChatOTapas.patch`
   - Ajoute `cubicracers-1.5.2.jar` (depuis build/libs/)

5. **Clique sur "Create issue"**

✅ **C'EST TOUT ! Beaucoup plus simple qu'un fork.**

---

### **Option 2: Merge Request via Fork (Plus complexe)**

Seulement si tu veux vraiment une MR :

1. **Supprime ton projet GitLab actuel**
2. **Fork le projet de Quentin via GitLab**
3. **Reconfigure tes remotes Git**
4. **Push ta branche**
5. **Crée la MR**

*(Voir instructions détaillées dans "Méthode 1" ci-dessus)*

---

## 📋 Checklist

- [x] Branche locale créée ✅
- [x] Patch créé ✅
- [x] JAR compilé ✅
- [x] Documentation complète ✅
- [ ] **Issue créée sur le projet de Quentin** ⚠️ (À FAIRE)
- [ ] Fichiers attachés (patch + JAR)
- [ ] Attendre retour du propriétaire

---

## 🎁 Fichiers à envoyer

**Dans ton projet local:**
```
📁 cubic-racers/
├── 📄 v1.5.2-LeChatOTapas.patch          ← À attacher à l'Issue
├── 📄 RESUME_PROPRIETAIRE.md             ← Résumé (optionnel)
├── 📄 CHANGELOG_1.5.2.md                 ← Détails (optionnel)
└── 📦 build/libs/cubicracers-1.5.2.jar   ← À attacher à l'Issue
```

---

## 💡 Pourquoi une Issue avec Patch est plus simple

1. **Pas besoin de fork** - Évite les complications GitLab
2. **Pas de problèmes de permissions** - Tout le monde peut créer une Issue
3. **Quentin peut appliquer facilement** - `git apply` en une commande
4. **C'est la méthode classique** - Utilisée par de nombreux projets Open Source

**Avantages du patch:**
- Quentin voit exactement ce qui change
- Il peut l'appliquer, le modifier, le refuser
- Pas de dépendance sur ton repo GitLab

---

## 🎯 ACTION IMMÉDIATE (5 minutes)

### **Crée l'Issue maintenant:**

1. **Va sur:** https://gitlab.com/Quentin3010/cubic-racers/-/issues/new
2. **Copie-colle** la description ci-dessus
3. **Attache** les 2 fichiers:
   - `v1.5.2-LeChatOTapas.patch`
   - `build/libs/cubicracers-1.5.2.jar`
4. **Clique** sur "Create issue"

**✅ TERMINÉ ! Plus simple que de gérer un fork.**

---

## 🔍 Si tu veux VRAIMENT créer un fork

```bash
# 1. Supprime le projet GitLab actuel (https://gitlab.com/LeChatOTapas/cubic-racers)

# 2. Fork via GitLab (bouton Fork sur https://gitlab.com/Quentin3010/cubic-racers)

# 3. Dans ton dépôt local:
git remote remove myfork
git remote add origin https://gitlab.com/LeChatOTapas/cubic-racers.git
git push origin feature/v1.5.2-network-fix

# 4. Maintenant tu peux créer une MR sur GitLab
```

---

Bonne chance avec ta contribution ! 🏎️💨

**LeChatOTapas**
