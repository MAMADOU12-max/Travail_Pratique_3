package csi.travail_pratique_3.models;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;


public class Balle {

    public Circle cercle;
    public double vitesseHorizontale, vitesseVerticale;
    public String type; // roche, papier, ciseau

    /**
     * Balle (constructeur)
     * publique (public)
     * Crée une nouvelle balle à la position donnée, avec le type
     * et l'image correspondants. La direction de départ est aléatoire.
     *
     * @param positionX  position horizontale de départ de la balle
     * @param positionY  position verticale de départ de la balle
     * @param type       type de la balle : "roche", "papier" ou "ciseau"
     * @param image      image affichée sur la balle
     */
    public Balle(double positionX, double positionY, String type, Image image) {

        this.type = type;

        cercle = new Circle(20); // rayon minimum
        cercle.setCenterX(positionX);
        cercle.setCenterY(positionY);

        cercle.setStrokeWidth(1);
        cercle.setStroke(javafx.scene.paint.Color.BLACK);

        cercle.setFill(new ImagePattern(image));

        // Vitesse aléatoire entre -2 et 2 (horizontale et verticale)
        vitesseHorizontale = Math.random() * 4 - 2;
        vitesseVerticale   = Math.random() * 4 - 2;
    }
}
