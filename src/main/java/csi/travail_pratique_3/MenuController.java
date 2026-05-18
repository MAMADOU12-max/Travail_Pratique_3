package csi.travail_pratique_3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class MenuController {

    @FXML
    private BorderPane root;  // conteneur principal où on affiche les vues

    // Une seule référence au controller du jeu cibles
    private CiblesController ciblesController = null;

    /**
     * initialize
     * privée (private)
     * Méthode appelée automatiquement au démarrage.
     * Elle charge la vue d'accueil par défaut.
     */
    @FXML
    private void initialize() {
        chargerVue("balles_rebondissantes-view.fxml");
    }

    // Arrête le jeu cibles a taper et l'audio si le jeu cibles est actif
    /**
     * arreterJeuSiActif
     * privée (private)
     * Méthode appelée pour arrêter la musique et l'audio
     * si le jeu cible à taper est actif
     */
    private void arreterJeuSiActif() {
        if (ciblesController != null) {
            ciblesController.arreterJeu();
            ciblesController = null;
        }
    }

    /**
     * allerBallesRebondissantes
     * privée (private)
     * Permet d'afficher la page du jeu balles rebondissantes
     */
    @FXML
    private void allerBallesRebondissantes() {
        arreterJeuSiActif();
        chargerVue("balles_rebondissantes-view.fxml");
    }

    /**
     * allerCiblesAtaper
     * privée (private)
     * Permet d'afficher la page du jeu cibles à taper.
     */
    @FXML
    private void allerCiblesAtaper() {
        arreterJeuSiActif();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/csi/travail_pratique_3/cibles_a_taper-view.fxml")
            );
            Parent vue = loader.load();
            root.setCenter(vue);

            // Sauvegarder le controller pour pouvoir l'arrêter plus tard
            ciblesController = loader.getController();

            Platform.runLater(() -> ciblesController.setScene(root.getScene()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * allerRegles
     * privée (private)
     * Permet d'afficher la page des régles du jeu cibles à taper.
     */
    @FXML
    private void allerRegles() {
        arreterJeuSiActif();
        chargerVue("regles_jeu_cibles-view.fxml");
    }

    /**
     * quitter
     * privée (private)
     * Permet de quitter l'application. Elle demande une confirmation.
     */
    @FXML
    void quitter() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter le jeu?");
        alert.setContentText("Confirmez votre choix.");

        Optional<ButtonType> choix = alert.showAndWait();

        if (choix.isPresent() && choix.get() == ButtonType.OK) {
            arreterJeuSiActif(); // arrêter proprement avant de quitter
            System.exit(0);
        }
    }

    /**
     * chargerVue
     * privée (private)
     * Permet de charger les differentes pages de notre menu.
     */
    private void chargerVue(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/csi/travail_pratique_3/" + fxml)
            );
            Parent view = loader.load();
            root.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}