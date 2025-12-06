# Tests E2E - Structure de Base

## 📁 Structure Créée

```
src/test/java/tests/e2e/
├── E2EUndoIsolationTest.java          # Parcours A : Isolation Undo/Redo
├── E2ECopyPasteInterViewTest.java      # Parcours B : Copy/Paste inter-vues
├── E2EPersistenceRestartTest.java      # Parcours C : Persistance & Redémarrage
├── E2ECommandEquivalenceTest.java      # Parcours D : Équivalence UI
└── README.md                           # Ce fichier

src/test/resources/test/
└── (dossier créé pour les ressources de test)
```

## ✅ Classes Créées

### 1. `E2EUndoIsolationTest`
**Parcours A** : Vérifier que Undo agit seulement sur la vue active

**Tests implémentés :**
- `testUndoOnlyAffectsActivePerspective()` - Isolation basique
- `testUndoRedoIsolationBetweenPerspectives()` - Isolation complète
- `testCommandHistoryPerPerspective()` - Vérification des piles séparées
- `testUndoAfterPerspectiveSwitch()` - Changement de perspective puis undo

### 2. `E2ECopyPasteInterViewTest`
**Parcours B** : Copier sur vue A / Coller sur vue B

**Tests implémentés :**
- `testCopyPasteBetweenDifferentPerspectives()` - Scénario principal
- `testCopyPasteWithCopyBothStrategy()` - Stratégie complète
- `testCopyPasteWithCopyScaleOnlyStrategy()` - Zoom uniquement
- `testCopyPasteWithCopyTranslationOnlyStrategy()` - Translation uniquement
- `testCopyPasteDoesNotAffectSourcePerspective()` - Isolation source
- `testUndoAfterPasteRestoresPreviousState()` - Undo après paste
- `testEmptyClipboardPasteDoesNothing()` - Robustesse

### 3. `E2EPersistenceRestartTest`
**Parcours C** : Redémarrage + Load reproduit l'affichage

**Tests implémentés :**
- `testSaveLoadRoundTrip()` - Cycle de base
- `testMultiplePerspectivesSaveLoad()` - Plusieurs perspectives
- `testLoadAfterRestartReproducesState()` - Simulation redémarrage
- `testSaveLoadWithSpecialCharacters()` - Noms avec caractères spéciaux
- `testSaveLoadWithExtremeValues()` - Valeurs limites
- `testLoadEmptyFileHandlesGracefully()` - Robustesse fichier vide

### 4. `E2ECommandEquivalenceTest`
**Parcours D** : Menu et geste souris déclenchent la même commande

**Tests implémentés :**
- `testZoomButtonAndMouseWheelEquivalent()` - Zoom équivalent
- `testPanDragEquivalent()` - Pan équivalent
- `testCommandBusUnifiedExecution()` - Unicité du bus
- `testUndoWorksAfterBothInteractionTypes()` - Undo après les deux types
- `testZoomInOutSymmetry()` - Symétrie zoom in/out

## 🚀 Exécution

### Lancer tous les tests E2E
```bash
mvn test -Dtest=tests.e2e.*
```

### Lancer un test spécifique
```bash
mvn test -Dtest=E2EUndoIsolationTest
mvn test -Dtest=E2ECopyPasteInterViewTest#testCopyPasteBetweenDifferentPerspectives
```

### Avec couverture
```bash
mvn clean test jacoco:report
# Résultat : target/site/jacoco/index.html
```

## ⚙️ Configuration

### Mode Headless
Tous les tests configurent automatiquement le mode headless pour Swing :
```java
System.setProperty("java.awt.headless", "true");
```

### Fichiers Temporaires
Les tests de persistance utilisent `@TempDir` (JUnit 5) pour créer des fichiers temporaires :
```java
@TempDir
Path tempDir;
```

## 📝 Notes d'Implémentation

### Image de Test
Actuellement, les tests utilisent `w2.jpg` à la racine du projet. Pour une meilleure isolation :
- Créer une petite image de test dans `src/test/resources/test/test-image.jpg`
- Modifier les tests pour utiliser cette image

### CommandHistory Singleton
`CommandHistory` est un singleton. Les tests sont isolés car chaque test crée de nouvelles perspectives, mais si nécessaire, on peut ajouter une méthode de reset dans `CommandHistory` pour les tests.

### Variables Non Utilisées
Quelques warnings sur des variables non utilisées ont été corrigés, mais certaines peuvent être utiles pour la lisibilité du code.

## 🔧 Prochaines Étapes

1. **Créer une image de test** dans `src/test/resources/test/`
2. **Vérifier que tous les tests compilent** : `mvn compile test-compile`
3. **Exécuter les tests** : `mvn test -Dtest=tests.e2e.*`
4. **Ajuster les tests** selon les résultats
5. **Ajouter JavaDoc** si nécessaire
6. **Configurer JaCoCo** pour la couverture

## 📚 Références

- Plan complet : `PLAN_TESTS_SYSTEM_E2E.md`
- Guide rapide : `E2E_TEST_QUICK_REFERENCE.md`

