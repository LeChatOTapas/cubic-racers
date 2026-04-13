# AGENTS.md

## Contexte rapide
- Mod Minecraft Forge `1.20.1` (`forge 47.3.0`) en Java 17.
- Point d'entree: `src/main/java/me/jesuismister/cubicracers/CubicRacers.java`.
- Convention IA existante detectee: `README.md` (placeholder), pas d'autre fichier d'instructions agent.

## Architecture a comprendre avant modification
- Initialisation centralisee dans `CubicRacers`: enregistrements Forge + `Network.init()` en `commonSetup`.
- Les karts sont decrits dans `CubicRacers.KARTS_DATA`; cette liste alimente:
  - `KartInit.initAllKarts()` (entites)
  - `ItemInit.initSpawnKartItem()` (items spawn)
- L'ordre d'init est critique (commentaires explicites dans `CubicRacers`): ne pas inverser `init...()` et `...register(bus)`.
- Logique gameplay principale: `entity/custom/TestKart.java` (physique, drift, boost, collisions, synchro).
- Reseau Forge `SimpleChannel`: `network/Network.java` avec IDs sequentiels via `nextId()` et `messageBuilder()`.

## Flux reseau (pattern projet)
- Client -> Serveur: seulement les inputs/actions (`InputSynchMessage`, `...UseMessage`).
- Serveur -> Clients: etat autoritaire (`KartPositionSyncMessage`, `StunMessage`, particules, item sync).
- Sync position en production: dans `TestKart.tick()`, envoi toutes les 2 ticks si kart pilote.
- Distribution reseau optimisee: `PacketDistributor.TRACKING_ENTITY_AND_SELF`.
- Cote client, interpolation via `kart.lerpTo(..., 5, true)` dans `KartPositionSyncMessage`.

## Frontieres client/serveur (tres important)
- Tout acces client (`Minecraft.getInstance`) doit rester derriere `DistExecutor` + `@OnlyIn(Dist.CLIENT)`.
- Exemple de garde correcte: `KartPositionSyncMessage.handle()` puis `handleOnClient()`.
- Eviter d'introduire des imports client dans du code serveur commun.

## Workflows dev utiles
- Build jar: `./gradlew.bat build`
- Run client dev: `./gradlew.bat runClient`
- Run serveur dev: `./gradlew.bat runServer`
- Data generation: `./gradlew.bat runData` (genere dans `src/generated/resources`)
- Si echec Gradle "Acces refuse" sous Windows/OneDrive, verifier droits sur `.gradle/` et verrouillage de fichiers.

## Ressources et integrations externes
- Config build: `build.gradle`, `gradle.properties`, `settings.gradle`.
- Mixins: `src/main/resources/cubicracers.mixins.json`, classe mixin `mixins/ClientLevelMixin.java`.
- Dependances majeures: ForgeGradle, Sponge Mixin, Geckolib (`4.2.1`), CoreLib (shaded/relocated).
- Relocation CoreLib configuree dans `shadowJar` (`de.maxhenkel.corelib` -> `de.maxhenkel.cubicracers.corelib`).

## Conventions locales a respecter
- Package racine unique: `me.jesuismister.cubicracers`.
- Messages reseau: triplet standard `encode/decode/handle` + `context.enqueueWork(...)`.
- Le dossier `src/test/java` est actuellement vide: valider manuellement en jeu multi avant merge des changements reseau/gameplay.
- Mettre a jour docs versionnees (`CHANGELOG_1.5.2.md`, etc.) si impact reseau ou compatibilite serveur.

