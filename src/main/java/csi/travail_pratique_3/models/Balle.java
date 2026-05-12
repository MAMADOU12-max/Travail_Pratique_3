package csi.travail_pratique_3.models;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Balle {

    public Circle cercle;
    public double dx, dy;
    public String type; // roche, papier, ciseau

    public Balle(double x, double y, String type, Image image) {

        this.type = type;

        cercle = new Circle(20); // rayon minimum
        cercle.setCenterX(x);
        cercle.setCenterY(y);

        cercle.setStrokeWidth(1);
        cercle.setStroke(javafx.scene.paint.Color.BLACK);

        cercle.setFill(new ImagePattern(image));

        // direction aléatoire
        dx = Math.random() * 4 - 2;
        dy = Math.random() * 4 - 2;
    }
}
