package csi.travail_pratique_3;

import csi.travail_pratique_3.models.Balle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import java.util.ArrayList;
import java.util.Random;

public class BallesController {

    @FXML
    private Pane zoneJeu;

    // Liste de toutes les balles dans le jeu
    private ArrayList<Balle> balles = new ArrayList<>();

    // Générateur de positions aléatoires
    private Random random = new Random();

    // Types possibles du jeu pierre-papier-ciseaux
    private String[] types = {"roche", "papier", "ciseau"};
    private int index = 0;

    // Images des balles
    private Image roche = new Image(getClass().getResourceAsStream("/assets/pierre.jpg"));
    private Image papier = new Image(getClass().getResourceAsStream("/assets/papier.jpg"));
    private Image ciseau = new Image(getClass().getResourceAsStream("/assets/ciseau.png"));

    // ÉTAT DU JEU : indique si le jeu est en pause
    private boolean enPause = false;

    /**
     * initialize
     * publique (public)
     * Méthode appelée automatiquement par JavaFX au chargement du FXML.
     * Elle démarre la boucle d'animation et configure la gestion de la pause
     * avec le clic de souris maintenu.
     */
    @FXML
    public void initialize() {

        // Animation principale du jeu (boucle infinie)
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                mettreAJour(); // mise à jour du jeu à chaque frame
            }
        };

        timer.start();

        // ==========================================================
        // GESTION DE LA PAUSE AVEC CLIC DE SOURIS MAINTENU
        // ==========================================================

        // Quand le joueur APPUIE sur la souris → on met le jeu en pause
        zoneJeu.setOnMousePressed(event -> {
            enPause = true; // blocage du jeu
        });

        // Quand le joueur RELÂCHE la souris → le jeu reprend
        zoneJeu.setOnMouseReleased(event -> {
            enPause = false; // reprise du jeu
        });
    }

    /**
     * ajouterUneBalle
     * privée (private)
     * Ajoute une nouvelle balle à une position aléatoire dans la zone de jeu.
     * Le type de la balle (roche, papier, ciseau) alterne à chaque appel.
     */
    @FXML
    private void ajouterUneBalle() {

        // Génération position aléatoire dans la zone de jeu
        double x = random.nextDouble() * (zoneJeu.getWidth() - 40) + 20;
        double y = random.nextDouble() * (zoneJeu.getHeight() - 40) + 20;

        // Type actuel (roche / papier / ciseau)
        String type = types[index];

        Image image;

        // Choix de l'image selon le type
        if (type.equals("roche")) {
            image = roche;
        } else if (type.equals("papier")) {
            image = papier;
        } else {
            image = ciseau;
        }

        // Création de la balle
        Balle balle = new Balle(x, y, type, image);

        // Ajout à la liste et à l'écran
        balles.add(balle);
        zoneJeu.getChildren().add(balle.cercle);

        // Passage au type suivant
        index++;

        if (index > 2) {
            index = 0;
        }
    }

    /**
     * ajouterTroisBalles
     * privée (private)
     * Ajoute trois balles d'un coup en appelant ajouterUneBalle() trois fois.
     */
    @FXML
    private void ajouterTroisBalles() {

        // Ajoute 3 balles d’un coup
        for (int i = 0; i < 3; i++) {
            ajouterUneBalle();
        }
    }

    /**
     * retirerBalle
     * privée (private)
     * Supprime la dernière balle ajoutée de la liste et de l'écran.
     * Ne fait rien si la liste est vide.
     */
    @FXML
    private void retirerBalle() {

        // Supprime la dernière balle si elle existe
        if (balles.size() > 0) {

            Balle balle = balles.remove(balles.size() - 1);
            zoneJeu.getChildren().remove(balle.cercle);
        }
    }

    /**
     * mettreAJour
     * privée (private)
     * Méthode appelée à chaque frame de l'animation.
     * Elle gère le mouvement des balles, les rebonds sur les bords
     * et les collisions entre balles.
     */
    private void mettreAJour() {

        // SI LE JEU EST EN PAUSE → ON BLOQUE TOUT
        if (enPause) return;

        // MOUVEMENT DES BALLES
        for (Balle balle : balles) {

            // déplacement horizontal et vertical
            balle.cercle.setCenterX(balle.cercle.getCenterX() + balle.vitesseHorizontale );
            balle.cercle.setCenterY(balle.cercle.getCenterY() + balle.vitesseVerticale);

            // rebond gauche / droite
            if (balle.cercle.getCenterX() <= 20 ||
                    balle.cercle.getCenterX() >= zoneJeu.getWidth() - 20) {

                balle.vitesseHorizontale  = -balle.vitesseHorizontale ;
            }

            // rebond haut / bas
            if (balle.cercle.getCenterY() <= 20 ||
                    balle.cercle.getCenterY() >= zoneJeu.getHeight() - 20) {

                balle.vitesseVerticale = -balle.vitesseVerticale;
            }
        }

        // GESTION DES COLLISIONS
        for (int i = 0; i < balles.size(); i++) {

            for (int j = i + 1; j < balles.size(); j++) {

                Balle b1 = balles.get(i);
                Balle b2 = balles.get(j);

                double dx = b1.cercle.getCenterX() - b2.cercle.getCenterX();
                double dy = b1.cercle.getCenterY() - b2.cercle.getCenterY();

                double distance = Math.sqrt(dx * dx + dy * dy);

                // si collision détectée
                if (distance <= 40) {
                    combat(b1, b2);
                }
            }
        }
    }

    /**
     * combat
     * privée (private)
     * Applique la logique pierre-papier-ciseaux entre deux balles en collision.
     * La balle perdante est transformée au type de la balle gagnante.
     */
    private void combat(Balle b1, Balle b2) {

        if (b1.type.equals(b2.type)) {
            return;
        }

        // roche bat ciseau
        if (b1.type.equals("roche") && b2.type.equals("ciseau")) {
            transformer(b2, "roche");
        }

        // papier bat roche
        else if (b1.type.equals("papier") && b2.type.equals("roche")) {
            transformer(b2, "papier");
        }

        // ciseau bat papier
        else if (b1.type.equals("ciseau") && b2.type.equals("papier")) {
            transformer(b2, "ciseau");
        }

        // sinon b1 perd
        else {
            transformer(b1, b2.type);
        }
    }

    /**
     * transformer
     * privée (private)
     * Change le type et l'image d'une balle selon le type donné en paramètre.
     * Utilisée quand une balle perd un combat.
     */
    private void transformer(Balle balle, String type) {

        balle.type = type;

        if (type.equals("roche")) {
            balle.cercle.setFill(new ImagePattern(roche));
        } else if (type.equals("papier")) {
            balle.cercle.setFill(new ImagePattern(papier));
        } else {
            balle.cercle.setFill(new ImagePattern(ciseau));
        }
    }
}