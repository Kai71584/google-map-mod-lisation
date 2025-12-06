# QUICK REFERENCE - TESTS E2E
## Guide rapide pour l'implémentation

---

## 📋 RÉSUMÉ DU PLAN

### 4 Parcours Critiques à Implémenter

1. **Parcours A : Undo Isolation** → `E2EUndoIsolationTest`
   - Vérifier que Undo n'affecte que la perspective active
   - ~4 tests

2. **Parcours B : Copy/Paste Inter-Vues** → `E2ECopyPasteInterViewTest`
   - Copier A → Coller sur B → Vérifier isolation
   - ~7 tests

3. **Parcours C : Persistance Restart** → `E2EPersistenceRestartTest`
   - Save → Load → Vérifier identité
   - ~6 tests

4. **Parcours D : Équivalence UI** → `E2ECommandEquivalenceTest`
   - Menu vs Souris → même commande
   - ~4 tests

**Total : ~20 tests E2E** (conforme à ~10% du total)

---

## 🏗️ STRUCTURE À CRÉER

```
src/test/java/tests/e2e/
├── E2EUndoIsolationTest.java
├── E2ECopyPasteInterViewTest.java
├── E2EPersistenceRestartTest.java
└── E2ECommandEquivalenceTest.java

src/test/resources/test/
├── test-image.jpg          # Image de test
└── test-data-*.json         # Templates JSON
```

---

## 🔧 DÉPENDANCES RECOMMANDÉES

Ajouter dans `pom.xml` (optionnel mais recommandé) :

```xml
<!-- Mockito pour mocks (si nécessaire) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>

<!-- AssertJ pour assertions fluides (optionnel) -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>

<!-- JaCoCo pour couverture -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Note :** JUnit 5 est déjà configuré ✅

---

## 🎯 TEMPLATE DE TEST E2E

```java
package tests.e2e;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

import model.*;
import command.*;
import controller.*;
import view.*;
import persistence.*;

public class E2EExampleTest {
    
    private ImageModel model;
    private Perspective perspective;
    private CommandBus bus;
    private ImageSource source;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Setup complet de l'application
        source = new FileImageSource("src/test/resources/test/test-image.jpg");
        model = new ImageModel(source);
        perspective = new Perspective("Test");
        model.addPerspective(perspective);
        bus = new CommandBus();
    }
    
    @Test
    void testExampleScenario() {
        // Arrange
        double initialScale = perspective.getScale();
        
        // Act
        bus.execute(new ZoomCommand(perspective, 1.5));
        
        // Assert
        assertEquals(initialScale * 1.5, perspective.getScale(), 0.001);
    }
    
    @AfterEach
    void tearDown() {
        // Nettoyage si nécessaire
    }
}
```

---

## ✅ CHECKLIST D'IMPLÉMENTATION

### Semaine 1
- [ ] Créer dossier `src/test/java/tests/e2e/`
- [ ] Créer dossier `src/test/resources/test/`
- [ ] Ajouter image de test `test-image.jpg`
- [ ] Implémenter `E2EUndoIsolationTest` (Parcours A)
- [ ] Vérifier que les tests passent

### Semaine 2
- [ ] Implémenter `E2ECopyPasteInterViewTest` (Parcours B)
- [ ] Implémenter `E2EPersistenceRestartTest` (Parcours C)
- [ ] Vérifier tous les tests

### Semaine 3
- [ ] Implémenter `E2ECommandEquivalenceTest` (Parcours D)
- [ ] Ajouter JavaDoc
- [ ] Configurer JaCoCo
- [ ] Générer rapports

---

## 🚀 COMMANDES UTILES

```bash
# Exécuter tous les E2E
mvn test -Dtest=tests.e2e.*

# Exécuter un test spécifique
mvn test -Dtest=E2EUndoIsolationTest

# Avec couverture
mvn clean test jacoco:report
# Résultat : target/site/jacoco/index.html

# Mode headless Swing (dans le code)
System.setProperty("java.awt.headless", "true");
```

---

## 📊 MÉTRIQUES CIBLES

- **Nombre de tests** : ~20 cas E2E
- **Temps d'exécution** : < 30 secondes
- **Taux de réussite** : 100%
- **Couverture E2E** : Focus sur parcours critiques uniquement

---

## 🔍 POINTS D'ATTENTION

1. **Mode Headless** : Configurer Swing en mode headless pour CI
2. **Fichiers temporaires** : Utiliser `@TempDir` (JUnit 5)
3. **Isolation** : Chaque test doit être indépendant
4. **Assertions** : Sur l'état du modèle, pas sur le rendu
5. **Singletons** : Réinitialiser `CommandHistory` si nécessaire

---

## 📚 RÉFÉRENCES RAPIDES

- **Plan complet** : `PLAN_TESTS_SYSTEM_E2E.md`
- **JUnit 5** : https://junit.org/junit5/docs/current/user-guide/
- **Mockito** : https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **AssertJ** : https://assertj.github.io/doc/

---

**Version :** 1.0  
**Dernière mise à jour :** Décembre 2025

