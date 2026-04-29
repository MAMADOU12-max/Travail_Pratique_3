package csi.travail_pratique_3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class HelloController {

    @FXML
    private BorderPane root; // conteneur principal où on affiche les vues


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

    /**
     * quitter
     * publique (public)
     * Affiche une alerte de confirmation avant de quitter l'application.
     */
    @FXML
    void quitter() {

        // Création de l'alerte
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter le jeu?");
        alert.setContentText("Confirmez votre choix.");

        // Récupération la réponse de l'utilisateur
        Optional<ButtonType> choix = alert.showAndWait();

        // Si l'utilisateur a choisi de quitter
        if (choix.isPresent() && choix.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    /**
     * chargerVue
     * privée (private)
     * Charge une vue FXML dans le centre du BorderPane.
     * @param fxml Nom du fichier FXML à charger.
     */
    private void chargerVue(String fxml) {
        try {
            // Chargement du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/csi/travail_pratique_3/" + fxml));
            Parent view = loader.load();
            // Placement de la vue au centre du BorderPane
            root.setCenter(view);
        } catch (Exception e) {
            // Affiche l'erreur en cas de problème
            e.printStackTrace();
        }
    }
}
