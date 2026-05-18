package csi.travail_pratique_3;

/**
 * =============================================================================
 * PATRON DE CONCEPTION : Observateur (Observer)
 * CATÉGORIE            : Comportemental
 * =============================================================================
 *
 * PROBLÈME :
 *   Dans le jeu, l'utilisateur peut changer de thème visuel (clair ou sombre)
 *   via une liste déroulante. Sans patron de conception, il faudrait vérifier
 *   manuellement à chaque action si le thème a changé, ce qui alourdit le code.
 *
 * CONTEXTE :
 *   Le ComboBox (seleteurTheme) peut changer de valeur à n'importe quel moment.
 *   Dès que l'utilisateur sélectionne un nouveau thème, l'application doit
 *   réagir immédiatement et automatiquement.
 *
 * SOLUTION — OÙ EST-IL APPLIQUÉ :
 *   Dans CiblesController.java, dans la méthode initialize().
 *   On utilise un "listener" sur la propriété valueProperty() du ComboBox.
 *   C'est le mécanisme explicite du patron Observateur en JavaFX.
 *
 *   Le ComboBox est le SUJET (celui qu'on observe).
 *   Le listener est l'OBSERVATEUR (celui qui réagit au changement).
 *
 *   Code appliqué :
 *   seleteurTheme.valueProperty().addListener((obs, oldV, newV) -> {
 *       themeSombre = newV.equals("Thème Sombre");
 *       appliquerCss();
 *   });
 *
 *   Dès que l'utilisateur change le thème, le listener est notifié
 *   automatiquement et appelle appliquerCss() pour mettre à jour l'interface.
 *
 * POURQUOI CE PATRON :
 *   Sans Observer, il faudrait vérifier manuellement la valeur du ComboBox
 *   à chaque clic ou action dans le jeu. Avec Observer, la réaction est
 *   automatique et immédiate, sans aucune vérification manuelle.
 *
 * GAINS :
 *   1. Réactivité : l'interface se met à jour instantanément sans intervention.
 *   2. Lisibilité : le code est clair — on sait exactement quoi surveiller.
 *   3. Séparation : la logique du thème est séparée de la logique du jeu.
 *   4. Extensible : on pourrait ajouter d'autres listeners sans modifier
 *                    le code existant (ex: changer la langue, la difficulté).
 * =============================================================================
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 500);

        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/regles-style.css").toExternalForm());
        // scene.getStylesheets().add(getClass().getResource("/styles/theme-sombre.css").toExternalForm());

        // Empêcher le redimensionnement
        stage.setResizable(false);

        stage.setTitle("Jeux!");
        stage.setScene(scene);

        // Fixer la taille minimale et maximale
        stage.setMinWidth(850);
        stage.setMinHeight(500);
        stage.setMaxWidth(850);
        stage.setMaxHeight(500);

        stage.show();
    }
}