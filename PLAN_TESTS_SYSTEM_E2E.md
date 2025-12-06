# PLAN DE TESTS SYSTÈME / END-TO-END (E2E)
## Application ImageViewer - Partie 2

**Responsable :** Alex
**Date :** Décembre 2025  
**Statut :** Plan initial

---

## 1. CONTEXTE & OBJECTIFS

### 1.1 Contexte
Ce plan couvre les tests **Système/E2E** pour l'application ImageViewer, dans le cadre du TP2 (INF1163). Les tests unitaires et d'intégration sont gérés par un collègue.

### 1.2 Objectifs
- Valider les **parcours critiques** de bout en bout
- Démontrer que **menu et gestes souris déclenchent la même commande**
- Vérifier l'**isolation des perspectives** (Undo sur vue active uniquement)
- Prouver la **fidélité de la persistance** (Save → Load → Restart)

### 1.3 Portée (~10% des tests)
Conformément à la pyramide de tests, les tests E2E sont **parcimonieux** et ciblent uniquement les scénarios critiques identifiés dans les exigences du TP1.

---

## 2. STRATÉGIE DE TEST E2E

### 2.1 Approche
- **Tests automatisés** avec JUnit 5
- **Mode headless** pour Swing (pas d'ouverture de fenêtres réelles)
- **Fixtures contrôlées** : images de test, fichiers JSON temporaires
- **Assertions sur l'état du modèle**, pas sur le rendu graphique

### 2.2 Outils & Technologies
- **JUnit 5** : Framework de test
- **Mockito** : Pour mocker les composants UI si nécessaire
- **AssertJ** (optionnel) : Assertions fluides
- **FEST-Swing** ou **AssertJ-Swing** (optionnel) : Tests Swing automatisés
- **Fichiers temporaires** : Pour la persistance (JUnit @TempDir)

### 2.3 Structure des Tests
```
src/test/java/
├── tests/
│   ├── unit/              # (Collègue)
│   ├── integration/       # (Collègue)
│   └── e2e/              # ← NOUVEAU
│       ├── E2EUndoIsolationTest.java
│       ├── E2ECopyPasteInterViewTest.java
│       ├── E2EPersistenceRestartTest.java
│       └── E2ECommandEquivalenceTest.java
└── resources/
    └── test/
        ├── test-image.jpg
        └── test-data.json
```

---

## 3. PARCOURS CRITIQUES IDENTIFIÉS

### 3.1 Parcours A : Undo agit seulement sur la vue active
**Exigence TP1 :** Undo/Redo sur la bonne vue

**Scénario :**
1. Créer 2 perspectives (A et B)
2. Appliquer des transformations sur A (zoom, pan)
3. Appliquer des transformations sur B (zoom, pan)
4. Basculer sur A
5. Faire Undo → doit annuler uniquement les actions de A
6. Basculer sur B
7. Faire Undo → doit annuler uniquement les actions de B

**Tests :**
- `testUndoOnlyAffectsActivePerspective()`
- `testUndoRedoIsolationBetweenPerspectives()`
- `testCommandHistoryPerPerspective()`

**Risque :** Historiques croisés entre perspectives

---

### 3.2 Parcours B : Copier sur vue A / Coller sur vue B
**Exigence TP1 :** Copier/coller des paramètres entre vues

**Scénario :**
1. Créer perspective A avec zoom=2.0, translation=(100, 50)
2. Créer perspective B avec zoom=1.0, translation=(0, 0)
3. Copier A (CopyBoth)
4. Coller sur B
5. Vérifier que B a exactement les mêmes paramètres que A
6. Vérifier que A n'a pas changé
7. Tester avec stratégies partielles (CopyScaleOnly, CopyTranslationOnly)

**Tests :**
- `testCopyPasteBetweenDifferentPerspectives()`
- `testCopyPasteWithDifferentStrategies()`
- `testCopyPasteDoesNotAffectSourcePerspective()`
- `testUndoAfterPasteRestoresPreviousState()`

**Risque :** Contamination entre perspectives, stratégies mal appliquées

---

### 3.3 Parcours C : Redémarrage + Load reproduit l'affichage attendu
**Exigence TP1 :** Persistance Save/Load fidèle

**Scénario :**
1. Créer plusieurs perspectives avec différents paramètres
2. Sauvegarder toutes les perspectives
3. Simuler un redémarrage (nouvelle instance de l'application)
4. Charger les perspectives depuis le fichier
5. Vérifier que chaque perspective a exactement les mêmes paramètres qu'avant

**Tests :**
- `testSaveLoadRoundTrip()`
- `testMultiplePerspectivesSaveLoad()`
- `testLoadAfterRestartReproducesState()`
- `testSaveLoadWithSpecialCharacters()`

**Risque :** Perte de données, corruption JSON, valeurs par défaut incorrectes

---

### 3.4 Parcours D : Menu et geste souris déclenchent la même commande
**Exigence TP1 :** Cohérence des interactions

**Scénario :**
1. Zoom via bouton "+" → vérifier état du modèle
2. Zoom via molette souris → vérifier même état du modèle
3. Pan via drag souris → vérifier état
4. Pan via bouton (si existe) → vérifier même état
5. Vérifier que les deux chemins utilisent le même CommandBus

**Tests :**
- `testZoomButtonAndMouseWheelEquivalent()`
- `testPanDragAndButtonEquivalent()`
- `testCommandBusUnifiedExecution()`

**Risque :** Incohérence entre UI et modèle

---

## 4. DÉTAIL DES TESTS E2E

### 4.1 Classe : `E2EUndoIsolationTest`

```java
package tests.e2e;

import org.junit.jupiter.api.*;
import model.*;
import command.*;
import controller.*;
import view.*;

/**
 * Tests E2E : Isolation Undo/Redo par perspective
 */
public class E2EUndoIsolationTest {
    
    private ImageModel model;
    private Perspective perspectiveA;
    private Perspective perspectiveB;
    private CommandBus bus;
    private UndoRedoController undoCtrl;
    
    @BeforeEach
    void setUp() {
        // Setup complet : modèle, perspectives, bus, contrôleurs
    }
    
    @Test
    void testUndoOnlyAffectsActivePerspective() {
        // Arrange : Actions sur A et B
        // Act : Undo sur A
        // Assert : Seulement A est affecté
    }
    
    @Test
    void testUndoRedoIsolationBetweenPerspectives() {
        // Vérifier que les historiques sont indépendants
    }
}
```

**Cas de test :**
1. `testUndoOnlyAffectsActivePerspective()` - Isolation basique
2. `testUndoRedoIsolationBetweenPerspectives()` - Isolation complète
3. `testCommandHistoryPerPerspective()` - Vérification des piles séparées
4. `testUndoAfterPerspectiveSwitch()` - Changement de perspective puis undo

---

### 4.2 Classe : `E2ECopyPasteInterViewTest`

```java
package tests.e2e;

/**
 * Tests E2E : Copy/Paste entre perspectives différentes
 */
public class E2ECopyPasteInterViewTest {
    
    @Test
    void testCopyPasteBetweenDifferentPerspectives() {
        // Copier A → Coller sur B → Vérifier B = A
    }
    
    @Test
    void testCopyPasteWithDifferentStrategies() {
        // Tester CopyBoth, CopyScaleOnly, CopyTranslationOnly
    }
    
    @Test
    void testCopyPasteDoesNotAffectSourcePerspective() {
        // Vérifier que A reste inchangé après copie
    }
    
    @Test
    void testUndoAfterPasteRestoresPreviousState() {
        // Paste puis Undo → état précédent restauré
    }
}
```

**Cas de test :**
1. `testCopyPasteBetweenDifferentPerspectives()` - Scénario principal
2. `testCopyPasteWithCopyBothStrategy()` - Stratégie complète
3. `testCopyPasteWithCopyScaleOnlyStrategy()` - Zoom uniquement
4. `testCopyPasteWithCopyTranslationOnlyStrategy()` - Translation uniquement
5. `testCopyPasteDoesNotAffectSourcePerspective()` - Isolation source
6. `testUndoAfterPasteRestoresPreviousState()` - Undo après paste
7. `testEmptyClipboardPasteDoesNothing()` - Robustesse

---

### 4.3 Classe : `E2EPersistenceRestartTest`

```java
package tests.e2e;

import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests E2E : Persistance et redémarrage
 */
public class E2EPersistenceRestartTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void testSaveLoadRoundTrip() {
        // Save → Load → Vérifier identité
    }
    
    @Test
    void testMultiplePerspectivesSaveLoad() {
        // Plusieurs perspectives → Save → Load → Toutes identiques
    }
    
    @Test
    void testLoadAfterRestartReproducesState() {
        // Simuler redémarrage complet
    }
}
```

**Cas de test :**
1. `testSaveLoadRoundTrip()` - Cycle de base
2. `testMultiplePerspectivesSaveLoad()` - Plusieurs perspectives
3. `testLoadAfterRestartReproducesState()` - Simulation redémarrage
4. `testSaveLoadWithSpecialCharacters()` - Noms avec caractères spéciaux
5. `testSaveLoadWithExtremeValues()` - Valeurs limites (zoom très grand/petit)
6. `testLoadEmptyFileHandlesGracefully()` - Robustesse fichier vide

---

### 4.4 Classe : `E2ECommandEquivalenceTest`

```java
package tests.e2e;

/**
 * Tests E2E : Équivalence menu vs gestes souris
 */
public class E2ECommandEquivalenceTest {
    
    @Test
    void testZoomButtonAndMouseWheelEquivalent() {
        // Bouton "+" vs molette → même état final
    }
    
    @Test
    void testPanDragAndButtonEquivalent() {
        // Drag souris vs (bouton si existe) → même état
    }
    
    @Test
    void testCommandBusUnifiedExecution() {
        // Vérifier que tous les chemins passent par CommandBus
    }
}
```

**Cas de test :**
1. `testZoomButtonAndMouseWheelEquivalent()` - Zoom équivalent
2. `testPanDragEquivalent()` - Pan équivalent
3. `testCommandBusUnifiedExecution()` - Unicité du bus
4. `testUndoWorksAfterBothInteractionTypes()` - Undo après les deux types

---

## 5. FIXTURES & DONNÉES DE TEST

### 5.1 Images de Test
- `test-image.jpg` : Image de test contrôlée (petite taille, ~100x100px)
- Créer dans `src/test/resources/test/`

### 5.2 Fichiers JSON de Test
- Fichiers temporaires créés avec `@TempDir` (JUnit 5)
- Templates pour différents scénarios :
  - `test-data-empty.json` : Fichier vide
  - `test-data-single.json` : Une perspective
  - `test-data-multiple.json` : Plusieurs perspectives

### 5.3 Setup/Teardown
- `@BeforeEach` : Créer modèle, perspectives, bus, contrôleurs
- `@AfterEach` : Nettoyer fichiers temporaires, réinitialiser singletons
- `@TempDir` : Pour fichiers de persistance

---

## 6. CRITÈRES DE RÉUSSITE

### 6.1 Critères Fonctionnels
- ✅ Tous les parcours critiques passent
- ✅ Isolation des perspectives vérifiée
- ✅ Copy/Paste fonctionne entre perspectives
- ✅ Save/Load fidèle (identité préservée)
- ✅ Menu et souris équivalents

### 6.2 Critères Techniques
- ✅ Tests exécutables en mode headless
- ✅ Pas de dépendances externes (réseau, fichiers système)
- ✅ Temps d'exécution < 30 secondes pour tous les E2E
- ✅ Tests idempotents (réexécutables)

### 6.3 Critères de Qualité
- ✅ Assertions claires et diagnostiquables
- ✅ Nommage explicite : `should[Action]_when[Condition]_expect[Result]`
- ✅ Documentation inline (JavaDoc)

---

## 7. MATRICE DE TRAÇABILITÉ E2E

| Exigence TP1 | Parcours E2E | Test(s) | Priorité |
|--------------|--------------|---------|----------|
| Undo/Redo sur la bonne vue | Parcours A | `E2EUndoIsolationTest.*` | **Critique** |
| Copier/coller des paramètres | Parcours B | `E2ECopyPasteInterViewTest.*` | **Critique** |
| Persistance Save/Load fidèle | Parcours C | `E2EPersistenceRestartTest.*` | **Critique** |
| Cohérence interactions UI | Parcours D | `E2ECommandEquivalenceTest.*` | **Moyenne** |

---

## 8. RISQUES & MITIGATION

### 8.1 Risques Identifiés

| Risque | Impact | Probabilité | Mitigation |
|--------|--------|-------------|------------|
| Tests Swing instables (timing) | Élevé | Moyenne | Mode headless, assertions sur modèle uniquement |
| Fichiers temporaires non nettoyés | Faible | Faible | `@TempDir`, `@AfterEach` |
| Tests dépendants entre eux | Moyen | Faible | Isolation complète, `@BeforeEach` |
| Couverture E2E insuffisante | Moyen | Faible | Focus sur parcours critiques uniquement |

### 8.2 Dette Technique Connue
- Pas de tests de rendu graphique (pixels) → acceptable (hors scope)
- Tests manuels complémentaires pour UX → documentés séparément

---

## 9. PLAN D'IMPLÉMENTATION

### Semaine 1 : Infrastructure
- [ ] Créer structure `src/test/java/tests/e2e/`
- [ ] Configurer mode headless Swing
- [ ] Créer fixtures de base (modèle, perspectives, bus)
- [ ] Implémenter `E2EUndoIsolationTest` (Parcours A)

### Semaine 2 : Parcours Critiques
- [ ] Implémenter `E2ECopyPasteInterViewTest` (Parcours B)
- [ ] Implémenter `E2EPersistenceRestartTest` (Parcours C)
- [ ] Vérifier que tous les tests passent

### Semaine 3 : Finalisation
- [ ] Implémenter `E2ECommandEquivalenceTest` (Parcours D)
- [ ] Documentation et JavaDoc
- [ ] Intégration dans le build Maven
- [ ] Génération des rapports

---

## 10. EXÉCUTION & RAPPORTS

### 10.1 Commandes Maven
```bash
# Exécuter tous les tests E2E
mvn test -Dtest=tests.e2e.*

# Exécuter un test spécifique
mvn test -Dtest=E2EUndoIsolationTest

# Avec couverture JaCoCo
mvn clean test jacoco:report
```

### 10.2 Rapports Attendus
- **Résultats JUnit XML** : `target/surefire-reports/`
- **Couverture JaCoCo** : `target/site/jacoco/index.html`
- **Logs de test** : Console + fichiers

### 10.3 Métriques Cibles
- **Nombre de tests E2E** : ~15-20 cas (conforme à ~10% du total)
- **Temps d'exécution** : < 30 secondes
- **Taux de réussite** : 100% (tests stables)

---

## 11. ANNEXES

### 11.1 Références
- [Pyramide de tests - Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ-Swing](https://assertj.github.io/doc/#assertj-swing-getting-started)

### 11.2 Notes
- Les tests E2E sont **complémentaires** aux tests unitaires/intégration
- Focus sur **valeur métier** et **parcours critiques**
- Éviter les tests redondants avec les niveaux inférieurs

---

**Version :** 1.0  
**Dernière mise à jour :** Décembre 2025  
**Statut :** ✅ Plan approuvé, prêt pour implémentation

