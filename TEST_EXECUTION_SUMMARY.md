# PLAN DE TEST - RÉSUMÉ EXÉCUTION
## Application ImageViewer (Manipulation d'image multi-perspectives)

---

## 1. PYRAMID DE TESTS

### Niveau 1 : Tests Unitaires ✅
| Test | Classe | Cas |
|------|--------|-----|
| **Scale** | `PerspectiveTest` | setScale (+), default (1.0), small values (0.5) |
| **Translation** | `PerspectiveTest` | setTranslation, default (0,0), negative values |
| **Memento** | `PerspectiveTest` | createMemento, restore, immutability |
| **Caretaker** | `MementoCaretakerTest` | save, pop, canUndo, canRedo, clear |
| **Clipboard** | `ClipboardMediatorTest` | mediateCopy, isEmpty, strategies |
| **Persistence** | `JsonPersistenceManagerTest` | save, load, multiple saves |

**Total Tests Unitaires : ~30 cas**

### Niveau 2 : Tests d'Intégration ✅
| Test | Cible | Cas |
|------|-------|-----|
| **Command + Memento** | `CommandIntegrationTest` | execute, undo, multiple actions |
| **Copy/Paste + Mediator** | `CopyPasteIntegrationTest` | copyPasteBoth, copyPasteXOnly, strategySwitch |

**Total Tests Intégration : ~10 cas**

### Niveau 3 : Tests Système (Manuel)
| Scénario | Type | Résultat |
|----------|------|---------|
| **SC1 : Zoom In/Out** | Manuel | À valider via GUI |
| **SC2 : Pan** | Manuel | À valider via GUI |
| **SC3 : Undo/Redo** | Manuel | À valider via GUI |
| **SC4 : Save** | Manuel | Vérifier data.json |
| **SC5 : Load** | Manuel | Vérifier ComboBox + paramètres |
| **SC6 : Multi-vues** | Manuel | Observer synchronisation |
| **SC7 : Copy/Paste** | Manuel | Tester toutes les stratégies |
| **SC8 : Robustesse** | Manuel | Fichiers invalides |

---

## 2. MATRICE DE TRAÇABILITÉ

### Exigence → Tests

| Exigence | Scénario | Tests Unitaires | Tests Intégration | Tests Système |
|----------|----------|------------------|-------------------|---------------|
| Zoom image | SC1 | `PerspectiveTest.testSetScalePositive` | `CommandIntegrationTest.testZoomCommandExecution` | SC1 |
| Translation | SC2 | `PerspectiveTest.testSetTranslation` | `CommandIntegrationTest.testZoomCommandExecution` | SC2 |
| Undo/Redo | SC3 | `MementoCaretakerTest.*` | `CommandIntegrationTest.testUndoMultipleActions` | SC3 |
| Sauvegarde JSON | SC4 | `JsonPersistenceManagerTest.testSavePerspective` | N/A | SC4 |
| Chargement JSON | SC5 | `JsonPersistenceManagerTest.testLoadEmptyFile` | N/A | SC5 |
| Multi-vues | SC6 | N/A | N/A | SC6 |
| Copy/Paste | SC7 | `ClipboardMediatorTest.*` | `CopyPasteIntegrationTest.*` | SC7 |
| Robustesse | SC8 | `JsonPersistenceManagerTest` (edge cases) | N/A | SC8 |

---

## 3. CRITÈRES D'ENTRÉE ✅

- [x] Image valide disponible (Capture d'écran)
- [x] Structure JSON initiale disponible (data.json)
- [x] Compilation sans erreurs
- [x] Vues affichent l'image par défaut
- [x] Framework de test (JUnit 5) configuré

---

## 4. CRITÈRES DE SORTIE (À VÉRIFIER)

### À la fin de l'exécution des tests :

- [ ] Tous les tests unitaires passent (30/30)
- [ ] Tous les tests d'intégration passent (10/10)
- [ ] Scénarios manuels valident les exigences (8/8)
- [ ] Aucun crash en conditions normales
- [ ] Aucun crash sur erreurs JSON (robustesse)
- [ ] Toutes les transformations sont cohérentes
- [ ] Undo/Redo fonctionne pour chaque action
- [ ] Aucune vue désynchronisée
- [ ] Les perspectives rechargées reproduisent exactement l'état sauvegardé
- [ ] Coverage de code > 60%

---

## 5. EXÉCUTION DES TESTS

### Lancer les tests unitaires + intégration :

```bash
mvn test
```

### Lancer un test spécifique :

```bash
mvn test -Dtest=PerspectiveTest
mvn test -Dtest=CommandIntegrationTest
```

### Vérifier la couverture :

```bash
mvn jacoco:report
# Résultats dans : target/site/jacoco/index.html
```

---

## 6. RÉSULTATS ATTENDUS

### Tests Unitaires
```
PerspectiveTest ...................... PASS (8 tests)
MementoCaretakerTest ................. PASS (8 tests)
ClipboardMediatorTest ................ PASS (8 tests)
JsonPersistenceManagerTest ........... PASS (4 tests)
Total Unit Tests ..................... 28/28 ✓
```

### Tests Intégration
```
CommandIntegrationTest ............... PASS (5 tests)
CopyPasteIntegrationTest ............ PASS (5 tests)
Total Integration Tests .............. 10/10 ✓
```

### Couverture Minimale Requise
```
- Perspective.java ................... 90%
- MementoCaretaker.java .............. 85%
- ClipboardMediator.java ............ 80%
- Command classes .................... 75%
```

---

## 7. SCÉNARIOS MANUELS À VALIDER

### SC1 : Zoom avec roulette souris
- [ ] Scroller vers le haut → zoom in (scale augmente)
- [ ] Scroller vers le bas → zoom out (scale diminue)
- [ ] Pas de distorsion
- [ ] CoordinatesView met à jour

### SC2 : Pan (glisser)
- [ ] Drag vers la droite → déplacement horizontal
- [ ] Drag vers le bas → déplacement vertical
- [ ] Translation X/Y correctes

### SC3 : Undo/Redo
- [ ] Undo revient à l'état précédent
- [ ] Redo refait l'action
- [ ] Historique correct sur 5+ actions

### SC4 : Save
- [ ] Bouton "Save" → dialogue de saisie
- [ ] Fichier data.json créé/modifié
- [ ] JSON valide et contient les bonnes données

### SC5 : Load
- [ ] ComboBox affiche les perspectives
- [ ] Sélectionner → applique les paramètres
- [ ] Zoom et pan correspondent

### SC6 : Multi-vues
- [ ] ImageView affiche correctement
- [ ] CoordinatesView synchronisé
- [ ] Pas de délai

### SC7 : Copy/Paste
- [ ] Copy → sauvegarde l'état
- [ ] Paste → restaure exactement
- [ ] Copy X + Paste X → colle que X
- [ ] Undo après Paste → restaure l'état précédent

### SC8 : Robustesse
- [ ] data.json vide → pas de crash
- [ ] JSON mal formé → message d'erreur clair
- [ ] Champs manquants → valeurs par défaut utilisées

---

## 8. DETTE CONNUE

- [ ] ThumbnailView n'affiche pas encore le rectangle du viewport
- [ ] Pas d'automatisation des tests de drag souris
- [ ] Encodage/caractères spéciaux partiellement gérés
- [ ] Pas de gestion partielle d'undo pour Copy/Paste

---

## 9. NOTES SUPPLÉMENTAIRES

- **Patterns appliqués** : Command, Memento, Observer, Mediator, Strategy, Composite
- **Coverage cible** : 70%+
- **Durée estimée de test manuel** : 30-45 minutes
- **Durée tests automatisés** : < 2 secondes

---

**Révisé le : 17 novembre 2025**
**Statut : En cours de mise en place**
