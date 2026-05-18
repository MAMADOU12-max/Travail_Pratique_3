package csi.travail_pratique_3;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.util.Random;

public class CiblesController {

    @FXML private GridPane grilleJeu;
    @FXML private Label etiquetteScore;
    @FXML private Label etiquetteTemps;
    @FXML private Label etiquetteStatut;
    @FXML private ComboBox<String> seleteurTheme;

    // Tableau des boutons de la grille 3x3
    private Button[][] boutons;

    // Variables du jeu
    private int score;
    private int tempsRestant;
    private int rangeCible;
    private int colonneCible;
    private boolean jeuEnCours;

    // Minuterie pour le chrono et le déplacement automatique
    private AnimationTimer minuterie;
    private long derniereChrono;
    private long dernierDeplacement;

    // Scène JavaFX pour appliquer le CSS
    private Scene scene;

    // Thème actif : false = clair (par défaut), true = sombre
    private boolean themeSombre = false;

    // Constantes du jeu
    private static final int TAILLE_GRILLE = 3;
    private static final int TEMPS_DEPART = 30;
    private static final long INTERVALLE_CHRONO = 1000; // 1 seconde en ms
    private static final long INTERVALLE_MOVE = 2000;   // 2 secondes en ms

    private Random hasard = new Random();

    // Objets audio
    private MediaPlayer musiqueFond;
    private Media sonReussite;
    private Media sonEchec;
    private boolean videoDejeuLancer = false;


    /**
     * initialize
     * privée (private)
     * Méthode appelée automatiquement par JavaFX au chargement du FXML.
     * Elle initialise la grille, les thèmes, les sons et démarre le jeu.
     */
    @FXML
    private void initialize() {

        boutons = new Button[TAILLE_GRILLE][TAILLE_GRILLE];

        // Remplir la liste déroulante des thèmes
        seleteurTheme.getItems().addAll("Thème Clair", "Thème Sombre");
        seleteurTheme.setValue("Thème Clair");

        // Patron Observateur : réagir automatiquement au changement de thème
        seleteurTheme.valueProperty().addListener((obs, oldV, newV) -> {
            themeSombre = newV.equals("Thème Sombre");
            appliquerCss();
        });

        chargerAudio();
        creerGrille();
        reinitialiserJeu();
    }


    /**
     * setScene
     * publique (public)
     * Reçoit la scène depuis le MenuController après le chargement du FXML.
     * Elle est nécessaire pour pouvoir appliquer les feuilles de style CSS.
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        appliquerCss();
    }

    /**
     * appliquerCss
     * privée (private)
     * Retire l'ancien thème et applique le nouveau fichier CSS
     * selon le thème sélectionné par l'utilisateur.
     */
    private void appliquerCss() {

        if (scene == null) return;

        // Retirer seulement les anciens thèmes, garder les autres CSS
        scene.getStylesheets().removeIf(style ->
                style.contains("theme-sombre.css") ||
                        style.contains("theme-clair.css")
        );

        String file = themeSombre
                ? "/styles/theme-sombre.css"
                : "/styles/theme-clair.css";

        scene.getStylesheets().add(
                getClass().getResource(file).toExternalForm()
        );

        // Forcer JavaFX à recalculer et appliquer les styles
        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }

    /**
     * chargerAudio
     * privée (private)
     * Charge la musique de fond et les sons du jeu.
     */
    private void chargerAudio() {

        try {
            // La musique de fond
            Media musique = new Media(getClass().getResource("/sons/musique.mp3").toExternalForm());
            musiqueFond = new MediaPlayer(musique);
            musiqueFond.setCycleCount(MediaPlayer.INDEFINITE); // jouer en boucle
            musiqueFond.setVolume(0.1); // diminuer le volume
            musiqueFond.play();

            sonReussite  = new Media(getClass().getResource("/sons/hit.mp3").toExternalForm());
            sonEchec = new Media(getClass().getResource("/sons/miss.mp3").toExternalForm());

        } catch (Exception e) {
            System.out.println("Erreur audio : " + e.getMessage());
        }
    }

    /**
     * jouerSon
     * privée (private)
     * Joue un son donné en paramètre.
     * Crée un nouveau MediaPlayer à chaque appel pour pouvoir rejouer le son.
     */
    private void jouerSon(Media son) {

        if (son == null) return;

        MediaPlayer mediaPlayer = new MediaPlayer(son);
        mediaPlayer.setVolume(2.0);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(mediaPlayer::dispose); // libère la mémoire après lecture
    }


    /**
     * creerGrille
     * privée (private)
     * Crée les 9 boutons de la grille 3x3 et les ajoute au GridPane.
     */
    private void creerGrille() {

        for (int i = 0; i < TAILLE_GRILLE; i++) {
            for (int j = 0; j < TAILLE_GRILLE; j++) {

                Button button = new Button();
                button.setText(String.valueOf(i * TAILLE_GRILLE + j + 1));
                button.getStyleClass().add("bouton-normal");

                int rangee = i;
                int colonne = j;

                button.setOnAction(e -> gererClic(rangee, colonne));

                boutons[i][j] = button;
                grilleJeu.add(button, j, i);
            }
        }
    }

    /**
     * gererClic
     * privée (private)
     * Gère le clic sur un bouton de la grille.
     * Si c'est la bonne cible : +1 point et déplacement.
     * Si c'est une mauvaise case : -1 seconde de pénalité.
     */
    private void gererClic(int rangeeCliquee, int colonneCliquee) {

        if (!jeuEnCours) return;

        if (rangeeCliquee == rangeCible && colonneCliquee == colonneCible) {

            // Bonne cible — point + déplacement
            score++;
            mettreAJourScore();
            jouerSon(sonReussite);
            deplacerCible();

        } else {

            // Mauvaise cible — pénalité d'1 seconde
            tempsRestant = Math.max(0, tempsRestant - 1);
            mettreAJourTemps();
            jouerSon(sonEchec);

            if (tempsRestant <= 0) {
                terminerJeu();
            }
        }
    }

    /**
     * deplacerCible
     * privée (private)
     * Déplace la cible verte vers une case différente de la position actuelle.
     */
    private void deplacerCible() {

        int nouvelleRangee, nouvelleColonne;

        // Choisir une case différente de la position actuelle
        do {
            nouvelleRangee = hasard.nextInt(TAILLE_GRILLE);
            nouvelleColonne = hasard.nextInt(TAILLE_GRILLE);
        } while (nouvelleRangee == rangeCible && nouvelleColonne == colonneCible);

        rangeCible   = nouvelleRangee;
        colonneCible = nouvelleColonne;

        rafraichirCouleurs();
    }

    /**
     * rafraichirCouleurs
     * privée (private)
     * Remet tous les boutons en style normal,
     * puis colore la cible en vert via la classe CSS bouton-cible.
     */
    private void rafraichirCouleurs() {

        for (int i = 0; i < TAILLE_GRILLE; i++)
            for (int j = 0; j < TAILLE_GRILLE; j++)
                boutons[i][j].getStyleClass().setAll("bouton-normal");

        boutons[rangeCible][colonneCible].getStyleClass().setAll("bouton-cible");
    }

    /**
     * mettreAJourScore
     * privée (private)
     * Met à jour l'étiquette du score affiché à l'écran.
     */
    private void mettreAJourScore() {
        etiquetteScore.setText("Score : " + score);
    }

    /**
     * mettreAJourTemps
     * privée (private)
     * Met à jour l'étiquette du temps restant affiché à l'écran.
     */
    private void mettreAJourTemps() {
        etiquetteTemps.setText("Temps : " + tempsRestant + "s");
    }

    /**
     * demarrerMinuterie
     * privée (private)
     * Démarre la minuterie qui gère le décompte du temps
     * et le déplacement automatique de la cible toutes les 2 secondes.
     */
    private void demarrerMinuterie() {

        derniereChrono     = System.currentTimeMillis();
        dernierDeplacement = System.currentTimeMillis();

        minuterie = new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (!jeuEnCours) return;

                long tempsActuel  = System.currentTimeMillis();

                // Décompter 1 seconde
                if (tempsActuel  - derniereChrono >= INTERVALLE_CHRONO) {
                    tempsRestant--;
                    mettreAJourTemps();
                    derniereChrono = tempsActuel ;

                    if (tempsRestant <= 0) {
                        terminerJeu();
                    }
                }

                // Déplacer la cible automatiquement toutes les 2 secondes
                if (tempsActuel  - dernierDeplacement >= INTERVALLE_MOVE) {
                    Platform.runLater(() -> deplacerCible());
                    dernierDeplacement = tempsActuel ;
                }
            }
        };

        minuterie.start();
    }

    /**
     * terminerJeu
     * privée (private)
     * Termine la partie : arrête la minuterie, désactive les boutons
     * et affiche la vidéo de fin.
     */
    private void terminerJeu() {

        if (!jeuEnCours) return; // empêche un double appel

        jeuEnCours = false;

        if (minuterie != null) minuterie.stop();

        etiquetteStatut.setText("Fin ! Score : " + score);

        // Désactiver tous les boutons
        for (Button[] ligne : boutons)
            for (Button b : ligne)
                b.setDisable(true);

        afficherVideoFin();
    }

    /**
     * afficherVideoFin
     * privée (private)
     * Affiche la vidéo de fin de partie dans une nouvelle fenêtre.
     */
    private void afficherVideoFin() {

        if (videoDejeuLancer) return;
        videoDejeuLancer = true;

        Platform.runLater(() -> {

            try {
                Media video = new Media(
                        getClass().getResource("/sons/video.mp4").toExternalForm()
                );

                MediaPlayer player = new MediaPlayer(video);
                MediaView view     = new MediaView(player);

                view.setFitWidth(800);
                view.setFitHeight(600);

                StackPane root = new StackPane(view);
                Scene scene    = new Scene(root, 800, 600);

                Stage stage = new Stage();
                stage.setTitle("Fin du jeu");
                stage.setScene(scene);
                stage.show();

                player.setAutoPlay(true);
                player.play();

                // Arrêter et libérer le lecteur quand on ferme la fenêtre
                stage.setOnCloseRequest(e -> {
                    player.stop();
                    player.dispose();
                    videoDejeuLancer = false;
                });

            } catch (Exception e) {
                e.printStackTrace();
                videoDejeuLancer = false;
            }
        });
    }

    /**
     * recommencer
     * privée (private)
     * Méthode liée au bouton Recommencer dans le FXML.
     * Elle réinitialise le jeu.
     */
    @FXML
    private void recommencer() {
        reinitialiserJeu();
    }

    /**
     * reinitialiserJeu
     * privée (private)
     * Remet toutes les variables du jeu à leur valeur de départ
     * et redémarre la minuterie.
     */
    private void reinitialiserJeu() {

        score        = 0;
        tempsRestant = TEMPS_DEPART;
        jeuEnCours   = true;

        mettreAJourScore();
        mettreAJourTemps();

        // Réactiver tous les boutons
        for (Button[] ligne : boutons)
            for (Button b : ligne) {
                b.setDisable(false);
                b.getStyleClass().setAll("bouton-normal");
            }

        // Choisir une position de départ aléatoire pour la cible
        rangeCible   = hasard.nextInt(TAILLE_GRILLE);
        colonneCible = hasard.nextInt(TAILLE_GRILLE);

        rafraichirCouleurs();

        if (minuterie != null) minuterie.stop();
        demarrerMinuterie();
    }

    /**
     * arreterAudio
     * publique (public)
     * Arrête et libère la musique de fond.
     * Appelée depuis le MenuController quand on change de page.
     */
    public void arreterAudio() {

        if (musiqueFond != null) {
            musiqueFond.stop();
            musiqueFond.dispose();
            musiqueFond = null;
        }
    }

    /**
     * arreterJeu
     * publique (public)
     * Arrête complètement le jeu : minuterie + audio.
     * Appelée depuis le MenuController quand on quitte la page du jeu.
     */
    public void arreterJeu() {
        jeuEnCours = false;
        if (minuterie != null) {
            minuterie.stop();
            minuterie = null;
        }
        arreterAudio();
    }
}