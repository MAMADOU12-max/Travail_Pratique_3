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

    private ArrayList<Balle> balles = new ArrayList<>();
    private Random random = new Random();

    private String[] types = {"roche", "papier", "ciseau"};
    private int index = 0;

    private Image roche = new Image(getClass().getResourceAsStream("/assets/pierre.jpg"));
    private Image papier = new Image(getClass().getResourceAsStream("/assets/papier.jpg"));
    private Image ciseau = new Image(getClass().getResourceAsStream("/assets/ciseau.png"));

    @FXML
    public void initialize() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        timer.start();
    }

    @FXML
    private void ajouterUneBalle() {

        double x = random.nextDouble() * (zoneJeu.getWidth() - 40) + 20;
        double y = random.nextDouble() * (zoneJeu.getHeight() - 40) + 20;

        String type = types[index];

        Image image;

        if (type.equals("roche")) {
            image = roche;
        } else if (type.equals("papier")) {
            image = papier;
        } else {
            image = ciseau;
        }

        Balle balle = new Balle(x, y, type, image);

        balles.add(balle);
        zoneJeu.getChildren().add(balle.cercle);

        index++;

        if (index > 2) {
            index = 0;
        }
    }

    @FXML
    private void ajouterTroisBalles() {

        for (int i = 0; i < 3; i++) {
            ajouterUneBalle();
        }
    }

    @FXML
    private void retirerBalle() {

        if (balles.size() > 0) {

            Balle balle = balles.remove(balles.size() - 1);
            zoneJeu.getChildren().remove(balle.cercle);
        }
    }

    private void update() {

        for (Balle balle : balles) {

            balle.cercle.setCenterX(balle.cercle.getCenterX() + balle.dx);
            balle.cercle.setCenterY(balle.cercle.getCenterY() + balle.dy);

            // rebond gauche/droite
            if (balle.cercle.getCenterX() <= 20 ||
                    balle.cercle.getCenterX() >= zoneJeu.getWidth() - 20) {

                balle.dx = -balle.dx;
            }

            // rebond haut/bas
            if (balle.cercle.getCenterY() <= 20 ||
                    balle.cercle.getCenterY() >= zoneJeu.getHeight() - 20) {

                balle.dy = -balle.dy;
            }
        }

        // collisions
        for (int i = 0; i < balles.size(); i++) {

            for (int j = i + 1; j < balles.size(); j++) {

                Balle b1 = balles.get(i);
                Balle b2 = balles.get(j);

                double dx = b1.cercle.getCenterX() - b2.cercle.getCenterX();
                double dy = b1.cercle.getCenterY() - b2.cercle.getCenterY();

                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= 40) {

                    combat(b1, b2);
                }
            }
        }
    }

    private void combat(Balle b1, Balle b2) {

        if (b1.type.equals(b2.type)) {
            return;
        }

        // roche gagne contre ciseau
        if (b1.type.equals("roche") && b2.type.equals("ciseau")) {
            transformer(b2, "roche");
        }

        else if (b1.type.equals("papier") && b2.type.equals("roche")) {
            transformer(b2, "papier");
        }

        else if (b1.type.equals("ciseau") && b2.type.equals("papier")) {
            transformer(b2, "ciseau");
        }

        else {
            transformer(b1, b2.type);
        }
    }

    private void transformer(Balle balle, String type) {

        balle.type = type;

        if (type.equals("roche")) {
            balle.cercle.setFill(new ImagePattern(roche));
        }

        else if (type.equals("papier")) {
            balle.cercle.setFill(new ImagePattern(papier));
        }

        else {
            balle.cercle.setFill(new ImagePattern(ciseau));
        }
    }
}